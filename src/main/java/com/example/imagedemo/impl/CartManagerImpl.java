package com.example.imagedemo.impl;

import com.example.imagedemo.common.ResponseDto;
import com.example.imagedemo.common.Status;
import com.example.imagedemo.dto.billResponseDto;
import com.example.imagedemo.dto.productRequestDto;
import com.example.imagedemo.dto.productResponseDto;
import com.example.imagedemo.service.cartOrderProductService;
import com.example.imagedemo.service.cartUseService;
import com.example.imagedemo.service.productService;
import com.example.imagedemo.service.userService;
import com.example.imagedemo.util.CartValidation;
import com.example.imagedemo.model.Cart;
import com.example.imagedemo.model.Product;
import com.example.imagedemo.model.CartOrderProductList;
import com.example.imagedemo.model.users;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Component
public class CartManagerImpl implements CartValidation {
    Logger logger = LoggerFactory.getLogger(CartManagerImpl.class);
    @Autowired
    private cartOrderProductService cartService;
    @Autowired
    private productService productService;
    @Autowired
    private userService userService;
    @Autowired
    private cartUseService cartUseService;

    @Override
    public ResponseDto<?> addProductToCart(productRequestDto P, String username, int requestId) throws Exception {
        users user = userService.getByUsername(username);
        int pid = P.getProductId();
        int quantity = P.getQuantity();
        Product p = productService.getSpecificProduct(pid);
        Set<String> productCode = Arrays.stream(p.getDeliveryPinCodes().split(",")).map(String::trim).collect(Collectors.toSet());
        if(!productCode.contains(P.getDeliveryPinCodes().trim())){
            logger.error("Product is not available for the provided pincode");
            return new ResponseDto<>(Status.NOT_FOUND.getStatusCode().value(),Status.NOT_FOUND.getStatusDescription(), requestId,"Product is not available at the provided pincode",null);
        }
        if(pid!=0 && p == null ){
            logger.error("The Product doesn't exists in the database");
            return new ResponseDto<>(Status.NOT_FOUND.getStatusCode().value(),Status.NOT_FOUND.getStatusDescription(),requestId,"Product doesn't exists in the database",null);
        }
        Cart c = user.getCart();
        CartOrderProductList ItemToAdd = cartService.getSpecificItems(c, p , "Active");
        if (p.getQuantity() > 0 && p.getQuantity() >= quantity) {
            if (ItemToAdd == null || ItemToAdd.getStatus().equals("Placed") || ItemToAdd.getStatus().equals("Returned") || ItemToAdd.getStatus().equals("Invoiced")) {
                CartOrderProductList itemsCart = new CartOrderProductList();
                itemsCart.setProduct(p);
                itemsCart.setCart(c);
                itemsCart.setQuantity(quantity);
                itemsCart.setCreatedAt(LocalDateTime.now());
                itemsCart.setUpdatedAt(LocalDateTime.now());
                itemsCart.setSeller(p.getSeller());
                itemsCart.setStatus("Active");
                cartService.additem(itemsCart);
                logger.info("Item added to cart successfully");
                c.setStatus("Active");
                cartUseService.setStatus(c);
                return new ResponseDto<>(Status.SUCCESS.getStatusCode().value(), Status.SUCCESS.getStatusDescription(), requestId, "Product added to the cart Successfully", p.getName());
            }
            ItemToAdd.setStatus("Active");
            ItemToAdd.setUpdatedAt(LocalDateTime.now());
            ItemToAdd.setQuantity(ItemToAdd.getQuantity() + quantity);
            cartService.additem(ItemToAdd);
            logger.info("Cart Updated Successfully for product {}", p.getName());
            return new ResponseDto<>(Status.SUCCESS.getStatusCode().value(), Status.SUCCESS.getStatusDescription(), requestId, "Cart updated for the existing product", p.getName());
        } else if (p.getQuantity() <= quantity) {
            logger.error("Quantity exceed the available quantity");
            return new ResponseDto<>(Status.BAD_REQUEST.getStatusCode().value(), Status.BAD_REQUEST.getStatusDescription(), requestId, "Exceed the available quantity", null);
        }
        logger.error("Product {} is out of stock", p.getName());
        return new ResponseDto<>(Status.NOT_FOUND.getStatusCode().value(), Status.NOT_FOUND.getStatusDescription(), requestId, "Currently Product is out of stock", null);
    }

    @Override
    public ResponseDto<?> removeProductFromCart(int pid, int requestId) throws Exception {
        Product p = productService.getSpecificProduct(pid);
        if(pid!= 0 && p==null){
            logger.error("The product doesn't exists in the cart");
            return new ResponseDto<>(Status.NOT_FOUND.getStatusCode().value(),Status.NOT_FOUND.getStatusDescription(), requestId,"Product deosn't exists in the cart",null);
        }
        logger.info("Removing product {} from the cart", p.getName());
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null || auth.getPrincipal().equals("anonymousUser")) {
            logger.error("No user logged in");
            return new ResponseDto<>(Status.BAD_REQUEST.getStatusCode().value(), Status.BAD_REQUEST.getStatusDescription(), requestId, "You need to logged in for accessing the cart", null);
        }
        users u = userService.getByUsername(auth.getName());
        Cart c = u.getCart();
        CartOrderProductList ItemToDelete = cartService.getSpecificItems(c, p, "Active");
        ItemToDelete.setStatus("Inactive");
        ItemToDelete.setQuantity(0);
        cartService.additem(ItemToDelete);
        logger.info("Item {} removed from the cart", p.getName());
        return new ResponseDto<>(Status.SUCCESS.getStatusCode().value(), Status.SUCCESS.getStatusDescription(), requestId, "Product removed from the cart", p.getPId());
    }

    @Override
    public ResponseDto<?> getAllProductsFromCart(int requestId, Pageable pageable) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        logger.info("User {} is trying to get all the items of the cart", auth.getName());
        if (auth == null || auth.getPrincipal().equals("anonymousUser")) {
            logger.error("No user logged in");
            return new ResponseDto<>(Status.BAD_REQUEST.getStatusCode().value(), Status.BAD_REQUEST.getStatusDescription(), requestId, "User need to logged in to access the cart", null);
        }
        users u = userService.getByUsername(auth.getName());
        Cart cart = cartUseService.getSpecificCart(u.getId());
        Page<CartOrderProductList> itemsCart = cartService.getAllItemsOfUser(cart, pageable, "Active");
        List<productResponseDto> response = new ArrayList<>();
        if (itemsCart == null || itemsCart.isEmpty()) {
            logger.warn("No item to show in the cart");
            return new ResponseDto<>(Status.NOT_FOUND.getStatusCode().value(), Status.NOT_FOUND.getStatusDescription(), requestId, "Cart is empty", null);
        }
        for (CartOrderProductList x : itemsCart) {
            productResponseDto ProductDto = new productResponseDto();
            ProductDto.setId(x.getProduct().getPId());
            ProductDto.setName(x.getProduct().getName());
            ProductDto.setPrice(x.getProduct().getPrice());
            ProductDto.setCategory(x.getProduct().getCategory());
            ProductDto.setDescription(x.getProduct().getDescription());
            ProductDto.setImageBase64(Base64.getEncoder().encodeToString(x.getProduct().getImageData()));
            ProductDto.setQuantity(x.getQuantity());
            response.add(ProductDto);
        }
        logger.info("All Items of Cart for user {} is fetched", u.getUsername());
        return new ResponseDto<>(Status.SUCCESS.getStatusCode().value(), Status.SUCCESS.getStatusDescription(), requestId, "All the items from the cart are fetched successfully", response);
    }

    @Override
    public ResponseDto<?> updateQuantity(int pId, int quantity, int requestId) {
        logger.info("Updating the quantity");
        Product p = productService.getSpecificProduct(pId);
        System.out.println(p + "produt is here");
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null || auth.getPrincipal().equals("anonymousUser")) {
            logger.error("No user logged in");
            return new ResponseDto<>(Status.BAD_REQUEST.getStatusCode().value(), Status.BAD_REQUEST.getStatusDescription(), requestId, "User need to logged in to access the cart", null);
        }
        users user = userService.getByUsername(auth.getName());
        Cart c = cartUseService.getSpecificCart(user.getId());
        System.out.println(c.getCartId() + "cart id");
//        System.out.println(c);
        CartOrderProductList ItemToUpdate = cartService.getSpecificItems(c, p,"Active");
        if (ItemToUpdate == null) {
            CartOrderProductList itemsCart = new CartOrderProductList();
            itemsCart.setProduct(p);
            itemsCart.setCart(c);
            itemsCart.setQuantity(quantity);
            itemsCart.setUpdatedAt(LocalDateTime.now());
            cartService.additem(itemsCart);
            logger.info("Item added to cart successfully");
            c.setStatus("Active");
            cartUseService.setStatus(c);
            return new ResponseDto<>(Status.SUCCESS.getStatusCode().value(), Status.SUCCESS.getStatusDescription(), requestId, "Product added to the cart Successfully", p.getName());
        }

        ItemToUpdate.setProduct(p);
        ItemToUpdate.setCart(c);
        ItemToUpdate.setQuantity(quantity);
        ItemToUpdate.setUpdatedAt(LocalDateTime.now());
        cartService.additem(ItemToUpdate);
        logger.info("Quantity of Product {} in cart is updated for user {}", p.getName(), user.getUsername());
        return new ResponseDto<>(Status.SUCCESS.getStatusCode().value(), Status.SUCCESS.getStatusDescription(), requestId, "Updated successfully", p.getName());
    }

    @Override
    public ResponseDto<?> getBillOfCart(int requestId, Pageable pageable) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        logger.info("User {} is trying to get all the items of the cart", auth.getName());
        if (auth == null || auth.getPrincipal().equals("anonymousUser")) {
            logger.error("No user logged in");
            return new ResponseDto<>(Status.BAD_REQUEST.getStatusCode().value(), Status.BAD_REQUEST.getStatusDescription(), requestId, "User need to logged in to access the cart", null);
        }
        users u = userService.getByUsername(auth.getName());
        Cart cart = cartUseService.getSpecificCart(u.getId());
        Page<CartOrderProductList> itemsCart = cartService.getAllItemsOfUser(cart, pageable, "Active");
        if (itemsCart == null || itemsCart.isEmpty()) {
            logger.warn("No item to show in the cart");
            return new ResponseDto<>(Status.NOT_FOUND.getStatusCode().value(), Status.NOT_FOUND.getStatusDescription(), requestId, "Cart is empty", null);
        }
        List<billResponseDto.ProductBillItems> billItems = new ArrayList<>();
        double subtotal = 0;
        billResponseDto billResponse = new billResponseDto();

        for (CartOrderProductList x : itemsCart) {
            billResponseDto.ProductBillItems response = new billResponseDto.ProductBillItems();
            response.setName(x.getProduct().getName());
            response.setPrice(x.getProduct().getPrice());
            response.setQuantity(x.getQuantity());
            response.setTotal(x.getProduct().getPrice() * x.getQuantity());
            billItems.add(response);
            subtotal += response.getTotal();
        }
        double discountAmount = 0;
        billResponse.setItems(billItems);
        billResponse.setSubtotal(subtotal);
        billResponse.setDiscountedAmount(discountAmount);
        subtotal -= discountAmount;
        billResponse.setAfterDiscountAmount(subtotal);
        billResponse.setGst(subtotal * 0.18);
        billResponse.setTotal(subtotal + subtotal * 0.18);
        logger.info("Bill for the cart items is generated for estimation");
        return new ResponseDto<>(Status.SUCCESS.getStatusCode().value(),Status.SUCCESS.getStatusDescription(), requestId,"Bill for the cart items is generated for estimation",billResponse);
    }
}
