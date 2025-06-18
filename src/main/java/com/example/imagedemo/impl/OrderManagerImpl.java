package com.example.imagedemo.impl;

import com.example.imagedemo.common.ResponseDto;
import com.example.imagedemo.common.Status;
import com.example.imagedemo.dto.billResponseDto;
import com.example.imagedemo.dto.productResponseDto;
import com.example.imagedemo.model.*;
import com.example.imagedemo.service.*;
import com.example.imagedemo.util.OrderValidation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

import java.lang.reflect.UndeclaredThrowableException;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Component
public class OrderManagerImpl implements OrderValidation {
    Logger logger = LoggerFactory.getLogger(OrderManagerImpl.class);
    @Autowired
    private cartOrderProductService cartService;
    @Autowired
    private orderCartService orderService;
    @Autowired
    private userService userService;
    @Autowired
    private cartUseService cartUseService;
    @Autowired
    private productService productService;
    @Autowired
    private couponService couponService;

    @Override
    public ResponseDto<?> placeOrder(int requestId, Pageable pageable) throws Exception {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        logger.info("User {} is placing a order", auth.getName());
        if (auth == null || auth.getPrincipal().equals("anonymousUser")) {
            logger.error("No user logged in");
            return new ResponseDto<>(Status.BAD_REQUEST.getStatusCode().value(), Status.BAD_REQUEST.getStatusDescription(), requestId, "No User is logged in", null);
        }
        users u = userService.getByUsername(auth.getName());
        OrderCart order = new OrderCart();
        Cart cart = u.getCart();
        Page<CartOrderProductList> itemsCart = cartService.getAllItemsOfUser(cart, pageable, "Active");
        List<billResponseDto.ProductBillItems> billItems = new ArrayList<>();
        double subtotal = 0;
        billResponseDto billResponse = new billResponseDto();
        if (itemsCart == null || itemsCart.isEmpty()) {
            logger.error("Cart is empty for {} so can't place order", u.getUsername());
            return new ResponseDto<>(Status.BAD_REQUEST.getStatusCode().value(), Status.BAD_REQUEST.getStatusDescription(), requestId, "No product in the cart to place a order", null);
        }
        for (CartOrderProductList x : itemsCart) {
            billResponseDto.ProductBillItems response = new billResponseDto.ProductBillItems();
            response.setName(x.getProduct().getName());
            response.setPrice(x.getProduct().getPrice());
            response.setQuantity(x.getQuantity());
            response.setTotal(x.getProduct().getPrice() * x.getQuantity());
            billItems.add(response);
            subtotal += response.getTotal();
        }
        order.setCart(cart);
        order.setStatus("Placed");
        order.setTotalPrice(subtotal);
        order.setFinalAmount(subtotal + subtotal * 0.18);
        order.setCreatedAt(LocalDateTime.now());
        order.setUpdatedAt(LocalDateTime.now());
        orderService.saveOrder(order);
        logger.info("Order {} produced successfully for {}", order.getOrderId(), u.getUsername());
        billResponse.setItems(billItems);
        billResponse.setSubtotal(subtotal);
        billResponse.setGst(subtotal * 0.18);
        billResponse.setTotal(subtotal + subtotal * 0.18);
        logger.info("Bill generated successfully for the order {} of rupees {}", order.getOrderId(), billResponse.getTotal());
        logger.info("Updating the quantity of all the products purchased in the product database");
        for (CartOrderProductList items : itemsCart) {
            Product p = items.getProduct();
            p.setQuantity(p.getQuantity() - items.getQuantity());
            if (p.getQuantity() <= 0) {
                p.setStatus("Out of stock");
            }
        }
        logger.info("Database of products updated successfully");
        logger.info("Clearing the cartitems for the user {}", u.getUsername());
        for (CartOrderProductList items : itemsCart) {
            items.setOrder(order);
            items.setStatus("Placed");
            cartService.additem(items);
        }
        logger.info("Cart is cleared");
        return new ResponseDto<>(Status.SUCCESS.getStatusCode().value(), Status.SUCCESS.getStatusDescription(), requestId, "Order is placed", billResponse);
    }

    @Override
    public ResponseDto<?> getAllOrders(int requestId, Pageable pageable) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        users u = userService.getByUsername(auth.getName());
        logger.info("fetching the list of all the orders of {}", u.getUsername());
        if (auth == null || auth.getPrincipal().equals("anonymousUser")) {
            logger.error("No user logged in");
            return new ResponseDto<>(Status.BAD_REQUEST.getStatusCode().value(), Status.BAD_REQUEST.getStatusDescription(), requestId, "No User is logged in", null);
        }
        Cart c = u.getCart();
        Page<OrderCart> order = orderService.getAllOrders(c, pageable, "Placed");
        List<Integer> orderIds = new ArrayList<>();
        for (OrderCart x : order) {
            orderIds.add(x.getOrderId());
        }
        logger.info("All the order for the {} are fetched successfully", u.getUsername());
        return new ResponseDto<>(Status.SUCCESS.getStatusCode().value(), Status.SUCCESS.getStatusDescription(), requestId, "Orders List fetched successfully", orderIds);
    }

    @Override
    public ResponseDto<?> getOrderByCustomDate(int requestId, long duration, String unit, Pageable pageable) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        logger.info("Fetching all the orders placed in last {} {}", duration, unit);
        if (auth == null || auth.getPrincipal().equals("anonymousUser")) {
            logger.error("No user logged in");
            return new ResponseDto<>(Status.BAD_REQUEST.getStatusCode().value(), Status.BAD_REQUEST.getStatusDescription(), requestId, "No User is logged in", null);
        }
        users u = userService.getByUsername(auth.getName());
        Cart c = u.getCart();
        String TimeUnit = unit.toUpperCase();
        ChronoUnit chronoUnit = null;
        chronoUnit = ChronoUnit.valueOf(TimeUnit);
        LocalDateTime fromDate = LocalDateTime.now().minus(duration, chronoUnit);
        Page<OrderCart> orders = orderService.getOrdersFromPast(c, "Placed", pageable, fromDate);
        List<Integer> orderIds = new ArrayList<>();
        for (OrderCart x : orders) {
            orderIds.add(x.getOrderId());
        }
        logger.info("All the orders placed in last {} {} are fetched successfully", duration, unit);
        return new ResponseDto<>(Status.SUCCESS.getStatusCode().value(), Status.SUCCESS.getStatusDescription(), requestId, "Orders List fetched successfully", orderIds);
    }

    @Override
    public ResponseDto<?> getAllProductsOfOrder(int requestId, Pageable pageable, int orderId) throws Exception {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        logger.info("Fetching all the Product Placed in the order number {}", orderId);
        if (auth == null || auth.getPrincipal().equals("anonymousUser")) {
            logger.error("No user logged in");
            return new ResponseDto<>(Status.BAD_REQUEST.getStatusCode().value(), Status.BAD_REQUEST.getStatusDescription(), requestId, "No User is logged in", null);
        }
        OrderCart order = orderService.getOrderById(orderId);
        if (order == null) {
            logger.error("Order id not found in the database ");
            return new ResponseDto<>(Status.NOT_FOUND.getStatusCode().value(), Status.NOT_FOUND.getStatusDescription(), requestId, "Order Id doesn't exists", null);
        }
        Page<CartOrderProductList> L = cartService.getAllProductsOfOrder(order, pageable);
        List<Product> ProductList = new ArrayList<>();
        for (CartOrderProductList l : L) {
            ProductList.add(l.getProduct());
        }
        List<productResponseDto> responseList = new ArrayList<>();
        for (Product items : ProductList) {
            productResponseDto dto = new productResponseDto();
            dto.setName(items.getName());
            dto.setImageBase64(Arrays.toString(items.getImageData()));
            dto.setPrice(items.getPrice());
            responseList.add(dto);
        }
        logger.info("All the products of the order number {} is fetched successfully", orderId);
        return new ResponseDto<>(Status.SUCCESS.getStatusCode().value(), Status.SUCCESS.getStatusDescription(), requestId, "All the products of the order are fetched", responseList);
    }

    @Override
    public ResponseDto<?> returnProductFromOrder(int requestId, int orderId, int pId, int quantity) throws Exception {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        Product p = productService.getSpecificProduct(pId);
        OrderCart order = orderService.getOrderById(orderId);
        logger.info("Requesting to return  product {} from the order {}", p.getName(), orderId);
        if (auth == null || auth.getPrincipal().equals("anonymousUser")) {
            logger.error("No user logged in");
            return new ResponseDto<>(Status.BAD_REQUEST.getStatusCode().value(), Status.BAD_REQUEST.getStatusDescription(), requestId, "No User is logged in", null);
        }
        users user = userService.getByUsername(auth.getName());
        Cart c = user.getCart();
        CartOrderProductList item = cartService.getProductToReturn(c, p, order);
        item.setStatus("Returned");
        cartService.additem(item);
        p.setQuantity(p.getQuantity() + quantity);
        double refundedAmount = p.getPrice() * quantity - order.getDiscountGivenInRs();
        if(refundedAmount<=0){
            refundedAmount=p.getPrice()*quantity;
        }
        order.setRefundedAmount(refundedAmount);
        double price = order.getTotalPrice();
        order.setFinalAmount(price - refundedAmount);
        productService.addProduct(p);
        orderService.saveOrder(order);
        logger.info("Returned request successfully completed");
        return new ResponseDto<>(Status.SUCCESS.getStatusCode().value(), Status.SUCCESS.getStatusDescription(), requestId, "Return nequest of the product is Placed successfully", p.getName());
    }

    @Override
    public ResponseDto<?> placeOrderWithCoupon(int requestId, Pageable pageable, int cId) throws Exception {
        Coupon coupon = couponService.findSpecificCoupon(cId);
        if (coupon == null) {
            logger.error("Please enter a valid coupon code");
            return new ResponseDto<>(Status.BAD_REQUEST.getStatusCode().value(), Status.BAD_REQUEST.getStatusDescription(), requestId, "Please enter a valid coupon", null);
        }
        String category = coupon.getCategory();
        boolean OfferApplied = false;
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        users u = userService.getByUsername(auth.getName());
        Cart cart = u.getCart();
        OrderCart order = new OrderCart();
        if (auth == null || auth.getPrincipal().equals("anonymousUser")) {
            logger.error("No user logged in");
            return new ResponseDto<>(Status.BAD_REQUEST.getStatusCode().value(), Status.BAD_REQUEST.getStatusDescription(), requestId, "User need to logged in to access the cart", null);
        }
        if (coupon.getCount() <= 0) {
            logger.error("The coupon {} is no more active", coupon.getCode());
            coupon.setStatus("Inactive");
            couponService.saveCoupon(coupon);
            return new ResponseDto<>(Status.BAD_REQUEST.getStatusCode().value(), Status.BAD_REQUEST.getStatusDescription(), requestId, "Coupon is no more active", null);
        }
        if (category.equals("Order") || category.equals("CustomOrder")) {
            logger.info("Applying coupon {} to the cart {}", coupon.getCode(), cart.getCartId());
            Page<CartOrderProductList> itemsCart = cartService.getAllItemsOfUser(cart, pageable, "Active");
            if (itemsCart == null || itemsCart.isEmpty()) {
                logger.error("Cart is empty for {} so can't place order", u.getUsername());
                return new ResponseDto<>(Status.BAD_REQUEST.getStatusCode().value(), Status.BAD_REQUEST.getStatusDescription(), requestId, "No product in the cart to place a order", null);
            }
            double totalItemsPrice = 0;
            for (CartOrderProductList items : itemsCart) {
                totalItemsPrice += items.getProduct().getPrice();
                System.out.println(totalItemsPrice);
            }
            if (totalItemsPrice > coupon.getOfferAvailableOn()) {
                logger.info("Offer is valid wait offer is applying and cart is getting updated");
                for (CartOrderProductList items : itemsCart) {
                    items.setCoupon(coupon);
                    cartService.additem(items);
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
                if (coupon.getDiscountUnit().equals("Percentage")) {
                    System.out.println(coupon.getDiscountValue());
                    discountAmount = (coupon.getDiscountValue() * totalItemsPrice) / 100;
                    System.out.println("Discount amount in percentage " + discountAmount);
                } else if (coupon.getDiscountUnit().equals("Price")) {
                    discountAmount = coupon.getDiscountValue();
                    System.out.println("Discounted amount in price" + discountAmount);
                }
                order.setCart(cart);
                order.setStatus("Placed");
                order.setTotalPrice(subtotal);
                order.setFinalAmount(subtotal + subtotal * 0.18 - discountAmount);
                order.setCreatedAt(LocalDateTime.now());
                order.setUpdatedAt(LocalDateTime.now());
                order.setCoupon(coupon);
                order.setDiscountGivenInRs((long) discountAmount);
                orderService.saveOrder(order);
                logger.info("Order {} produced successfully for {} after apllying the coupon {}", order.getOrderId(), u.getUsername(), coupon.getCode());
                billResponse.setItems(billItems);
                billResponse.setSubtotal(subtotal);
                billResponse.setDiscountedAmount(discountAmount);
                subtotal -= discountAmount;
                billResponse.setAfterDiscountAmount(subtotal);
                billResponse.setGst(subtotal * 0.18);
                billResponse.setTotal(subtotal + subtotal * 0.18);
                logger.info("Bill generated successfully for the order {} of rupees {} after applying coupon {}", order.getOrderId(), billResponse.getTotal(), coupon.getCode());
                logger.info("Updating  quantity of all the products purchased in the product database");
                for (CartOrderProductList items : itemsCart) {
                    Product p = items.getProduct();
                    p.setQuantity(p.getQuantity() - items.getQuantity());
                    if (p.getQuantity() <= 0) {
                        p.setStatus("Out of stock");
                    }
                }
                logger.info("Product database updated successfully");
                logger.info("Clearing the cartitems for the user {} as order placed", u.getUsername());
                for (CartOrderProductList items : itemsCart) {
                    items.setOrder(order);
                    items.setStatus("Placed");
                    cartService.additem(items);
                }
                logger.info("Database of the coupon is updating");
                int count = coupon.getCount();
                coupon.setCount(count - 1);
                couponService.saveCoupon(coupon);
                logger.info("Cart is cleared successfully");
                return new ResponseDto<>(Status.SUCCESS.getStatusCode().value(), Status.SUCCESS.getStatusDescription(), requestId, "Order is placed after apllying coupon", billResponse);
            }
        }
        if (category.equals("Product") || category.equals("CustomProduct")) {
            logger.info("Applying coupon {} to the Product", coupon.getCode());
            Page<CartOrderProductList> itemsCart = cartService.getAllItemsOfUser(cart, pageable, "Active");
            if (itemsCart == null || itemsCart.isEmpty()) {
                logger.error("Cart is empty for {} so can't place order", u.getUsername());
                return new ResponseDto<>(Status.BAD_REQUEST.getStatusCode().value(), Status.BAD_REQUEST.getStatusDescription(), requestId, "No product in the cart to place a order", null);
            }
            for (CartOrderProductList items : itemsCart) {
                if (items.getProduct() == coupon.getProduct() && items.getQuantity() >= coupon.getOfferAvailableOn()) {
                    OfferApplied = true;
                    items.setCoupon(coupon);
                }
            }
            double discountAmount = 0;
            List<billResponseDto.ProductBillItems> billItems = new ArrayList<>();
            double subtotal = 0;
            billResponseDto billResponse = new billResponseDto();

            for (CartOrderProductList x : itemsCart) {
                billResponseDto.ProductBillItems response = new billResponseDto.ProductBillItems();
                response.setName(x.getProduct().getName());
                response.setPrice(x.getProduct().getPrice());
                response.setQuantity(x.getQuantity());
                response.setTotal(x.getProduct().getPrice() * x.getQuantity());
                if (coupon.getDiscountUnit().equals("Percentage")) {
                    discountAmount = coupon.getDiscountValue() / 100 * response.getTotal();
                } else if (coupon.getDiscountUnit().equals("Price")) {
                    discountAmount = coupon.getDiscountValue();
                }
                response.setDiscount(discountAmount);
                billItems.add(response);
                subtotal += response.getTotal() - discountAmount;
            }
            order.setCart(cart);
            order.setStatus("Placed");
            order.setTotalPrice(subtotal);
            order.setFinalAmount(subtotal + subtotal * 0.18);
            order.setCreatedAt(LocalDateTime.now());
            order.setUpdatedAt(LocalDateTime.now());
            order.setCoupon(coupon);
            order.setDiscountGivenInRs((long) discountAmount);
            orderService.saveOrder(order);
            logger.info("Order {} produced successfully for {} after apllying the coupon {}", order.getOrderId(), u.getUsername(), coupon.getCode());
            billResponse.setItems(billItems);
            billResponse.setItems(billItems);
            billResponse.setSubtotal(subtotal);
            billResponse.setGst(subtotal * 0.18);
            billResponse.setTotal(subtotal + subtotal * 0.18);
            logger.info("Bill generated successfully for the order {} of rupees {} after applying coupon {}", order.getOrderId(), billResponse.getTotal(), coupon.getCode());
            logger.info("Updating  quantity of all the products purchased in the product database");
            for (CartOrderProductList items : itemsCart) {
                Product p = items.getProduct();
                p.setQuantity(p.getQuantity() - items.getQuantity());
                if (p.getQuantity() <= 0) {
                    p.setStatus("Out of stock");
                }
            }
            logger.info("Product database updated successfully");
            logger.info("Clearing the cartitems for the user {} as order placed", u.getUsername());
            for (CartOrderProductList items : itemsCart) {
                items.setOrder(order);
                items.setStatus("Placed");
                cartService.additem(items);
            }
            logger.info("Database of the coupon is updating");
            int count = coupon.getCount();
            coupon.setCount(count - 1);
            couponService.saveCoupon(coupon);
            logger.info("Cart is cleared successfully");
            return new ResponseDto<>(Status.SUCCESS.getStatusCode().value(), Status.SUCCESS.getStatusDescription(), requestId, "Order is placed after applying coupon", billResponse);
        }
        logger.error("The conditions of the coupon {} are not full filled so failed to process the order", coupon.getCode());
        return new ResponseDto<>(Status.BAD_REQUEST.getStatusCode().value(), Status.BAD_REQUEST.getStatusDescription(), requestId, "The conditions of the coupon are not fulfilled so failed to process the order", null);
    }
}
