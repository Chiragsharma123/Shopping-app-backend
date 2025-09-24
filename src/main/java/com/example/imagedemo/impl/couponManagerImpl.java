package com.example.imagedemo.impl;

import com.example.imagedemo.common.ResponseDto;
import com.example.imagedemo.common.Status;
import com.example.imagedemo.dto.activeCouponResponseDto;
import com.example.imagedemo.dto.billResponseDto;
import com.example.imagedemo.dto.couponRequestDto;
import com.example.imagedemo.dto.productResponseDto;
import com.example.imagedemo.model.*;
import com.example.imagedemo.service.*;
import com.example.imagedemo.util.couponValidation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;

@Component
public class couponManagerImpl implements couponValidation {
    Logger logger = LoggerFactory.getLogger(couponManagerImpl.class);
    @Autowired
    private couponService couponService;
    @Autowired
    private productService productService;
    @Autowired
    private userService userService;
    @Autowired
    private cartUseService cartUseService;
    @Autowired
    private cartOrderProductService cartService;

    @Override
    public ResponseDto<?> createDiscountCoupon(int requestId, couponRequestDto couponRequestDto) throws Exception {
        logger.info("Creating a new coupon");
        if (couponRequestDto == null) {
            logger.error("Request body can't be null");
            return new ResponseDto<>(Status.BAD_REQUEST.getStatusCode().value(), Status.BAD_REQUEST.getStatusDescription(), requestId, "Please provide the body for request", null);
        }
        int pId = couponRequestDto.getPId();
        Product product = productService.getSpecificProduct(pId);
        if (couponRequestDto.getCategory().equals("Product") && product == null) {
            logger.error("There is no product found for the provided id");
            return new ResponseDto<>(Status.NOT_FOUND.getStatusCode().value(), Status.NOT_FOUND.getStatusDescription(), requestId, "No product found in the database", null);
        }
        Coupon couponToAdd = couponService.findSpecificCoupon(couponRequestDto.getCouponId());
        if (couponToAdd == null && couponRequestDto.getCouponId() != 0) {
            logger.error("The coupon doesn't exists in the database please provide a valid coupon id");
            return new ResponseDto<>(Status.NOT_FOUND.getStatusCode().value(), Status.NOT_FOUND.getStatusDescription(), requestId, "The coupon doesn't exists in the database please provide a valid coupon id", null);
        }
        ChronoUnit chronoUnit = ChronoUnit.valueOf(couponRequestDto.getUnit().toUpperCase());
        LocalDateTime expiryTime = LocalDateTime.now().plus(couponRequestDto.getDuration(), chronoUnit);
        if (couponToAdd == null) {
            Coupon coupon = new Coupon();
            coupon.setCode(couponRequestDto.getCode());
            coupon.setCategory(couponRequestDto.getCategory());
            coupon.setCount(couponRequestDto.getCount());
            coupon.setDescription(couponRequestDto.getDescription());
            coupon.setDiscountUnit(couponRequestDto.getDiscountUnit());
            coupon.setDiscountValue(couponRequestDto.getDiscountValue());
            coupon.setOfferAvailableOn(couponRequestDto.getOfferAvailableOn());
            coupon.setProduct(product);
            if (couponRequestDto.getCount() > 0) {
                coupon.setStatus("Active");
            } else {
                coupon.setStatus("Inactive");
            }
            coupon.setExpiresAt(expiryTime);
            couponService.saveCoupon(coupon);

            logger.info("Coupon is created successfully");
            return new ResponseDto<>(Status.CREATED.getStatusCode().value(), Status.CREATED.getStatusDescription(), requestId, "Coupon is created sucessfully", coupon.getCode());
        }
        if (couponRequestDto.getCode() != null) {
            couponToAdd.setCode(couponRequestDto.getCode());
        }
        if (couponRequestDto.getCategory() != null) {
            couponToAdd.setCategory(couponRequestDto.getCategory());
        }
        if (couponRequestDto.getCount() != 0) {
            couponToAdd.setCount(couponRequestDto.getCount());
        }
        if (couponRequestDto.getDescription() != null) {
            couponToAdd.setDescription(couponRequestDto.getDescription());
        }
        if (couponRequestDto.getDiscountUnit() != null) {
            couponToAdd.setDiscountUnit(couponRequestDto.getDiscountUnit());
        }
        if (couponRequestDto.getDiscountValue() != 0) {
            couponToAdd.setDiscountValue(couponRequestDto.getDiscountValue());
        }
        if (couponRequestDto.getOfferAvailableOn() != 0) {
            couponToAdd.setOfferAvailableOn(couponRequestDto.getOfferAvailableOn());
        }
        if (product != null) {
            couponToAdd.setProduct(product);
        }
        if (couponRequestDto.getCount() > 0) {
            couponToAdd.setStatus("Active");
        } else {
            couponToAdd.setStatus("Inactive");
        }
        if (couponRequestDto.getExpiresAt() != null) {
            couponToAdd.setExpiresAt(expiryTime);
        }
        couponService.saveCoupon(couponToAdd);
        logger.info("Coupon is updated successfully");
        return new ResponseDto<>(Status.CREATED.getStatusCode().value(), Status.CREATED.getStatusDescription(), requestId, "Coupon is updated successfully", couponToAdd.getCode());
    }

    @Override
    public ResponseDto<?> getAllActiveCoupon(int requestId) {
        logger.info("Fetching the list of all the active coupons");
        LocalDateTime time = LocalDateTime.now();
        List<Coupon> ExpiredCoupons = couponService.getAllExpiryCoupons("Active", 0, time);
        for (Coupon expiry : ExpiredCoupons) {
            expiry.setStatus("Expired");
            couponService.saveCoupon(expiry);
        }
        List<Coupon> ActiveCoupons = couponService.getAllActiveCoupons("Active");
        if (ActiveCoupons == null) {
            logger.info("No active coupon is present right now");
            return new ResponseDto<>(Status.NOT_FOUND.getStatusCode().value(), Status.NOT_FOUND.getStatusDescription(), requestId, "No active coupon is present right now", null);
        }
        List<activeCouponResponseDto> activeCouponResponseDtoList = new ArrayList<>();
        for (Coupon items : ActiveCoupons) {
            activeCouponResponseDto ActiveCouponsDto = new activeCouponResponseDto();
            ActiveCouponsDto.setId(items.getCId());
            ActiveCouponsDto.setCode(items.getCode());
            ActiveCouponsDto.setDescription(items.getDescription());
            ActiveCouponsDto.setExpiresAt(items.getExpiresAt());
            activeCouponResponseDtoList.add(ActiveCouponsDto);
        }
        logger.info("List of all the active coupons are fetched successfully");
        return new ResponseDto<>(Status.SUCCESS.getStatusCode().value(), Status.SUCCESS.getStatusDescription(), requestId, "All Active coupons are fetched successfully", activeCouponResponseDtoList);
    }

    @Override
    public ResponseDto<?> ApplyCoupon(int requestId, couponRequestDto c) throws Exception {
        if (c.getCouponId() == 0) {
            logger.error("Please provide the id of the coupon you want to apply");
            return new ResponseDto<>(Status.BAD_REQUEST.getStatusCode().value(), Status.BAD_REQUEST.getStatusDescription(), requestId, "Please provide the id of the coupon ", null);
        }
        Coupon coupon = couponService.findSpecificCoupon(c.getCouponId());
        if (c.getCouponId() != 0 && coupon == null) {
            logger.error("Please enter a valid coupon the coupon doesn't exists");
            return new ResponseDto<>(Status.NOT_FOUND.getStatusCode().value(), Status.NOT_FOUND.getStatusDescription(), requestId, "Please enter a valid coupon ", null);
        }
        Pageable pageable = PageRequest.of(c.getPagingDto().getPage(), c.getPagingDto().getSize());
        String category = coupon.getCategory();
        boolean OfferApplied = false;
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        users u = userService.getByUsername(auth.getName());
        Cart cart = u.getCart();
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
                totalItemsPrice += items.getProduct().getPrice() * items.getQuantity();
                System.out.println(totalItemsPrice);
            }
            if (totalItemsPrice > coupon.getOfferAvailableOn()) {
                logger.info("Offer is valid wait offer is applying and cart is getting updated");
                for (CartOrderProductList items : itemsCart) {
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
                billResponse.setItems(billItems);
                billResponse.setSubtotal(subtotal);
                billResponse.setDiscountedAmount(discountAmount);
                subtotal -= discountAmount;
                billResponse.setAfterDiscountAmount(subtotal);
                billResponse.setGst(subtotal * 0.18);
                billResponse.setTotal(subtotal + subtotal * 0.18);
                logger.info("Coupon {} offer applied successfully on the cart {}", coupon.getCode(), cart.getCartId());
                return new ResponseDto<>(Status.SUCCESS.getStatusCode().value(), Status.SUCCESS.getStatusDescription(), requestId, "Coupon applied successfully", billResponse);
            }
        }
        if (category.equals("Product") || category.equals("CustomProduct")) {
            logger.info("Applying coupon {} to the Product", coupon.getCode());
            Page<CartOrderProductList> itemsCart = cartService.getAllItemsOfUser(cart, pageable, "Active");
            if (itemsCart == null || itemsCart.isEmpty()) {
                logger.error("Cart is empty for {} so can't place order", u.getUsername());
                return new ResponseDto<>(Status.BAD_REQUEST.getStatusCode().value(), Status.BAD_REQUEST.getStatusDescription(), requestId, "No product in the cart to place a order", null);
            }
            double discountAmount = 0;
            double totalAmount = 0;
            double totalDiscount = 0;
            List<billResponseDto.ProductBillItems> billItems = new ArrayList<>();
            double subtotal = 0;
            billResponseDto billResponse = new billResponseDto();

            for (CartOrderProductList x : itemsCart) {
                double total = x.getProduct().getPrice() * x.getQuantity();
                billResponseDto.ProductBillItems response = new billResponseDto.ProductBillItems();
                response.setName(x.getProduct().getName());
                response.setPrice(x.getProduct().getPrice());
                response.setQuantity(x.getQuantity());
                response.setTotal(total);
                if (x.getProduct() == coupon.getProduct() && x.getQuantity() >= coupon.getOfferAvailableOn()) {
                    if (coupon.getDiscountUnit().equals("Percentage")) {
                        discountAmount = coupon.getDiscountValue() / 100 * response.getTotal();
                    } else if (coupon.getDiscountUnit().equals("Price")) {
                        discountAmount = coupon.getDiscountValue();
                    }
                    response.setDiscount(discountAmount);
                    totalDiscount += discountAmount;
                }
                subtotal = total - totalDiscount;
                totalAmount += subtotal;
                billItems.add(response);
            }
            billResponse.setDiscountedAmount(totalDiscount);
            billResponse.setItems(billItems);
            billResponse.setSubtotal(totalAmount);
            billResponse.setGst(totalAmount * 0.18);
            billResponse.setTotal(totalAmount + totalAmount * 0.18);
            logger.info("Coupon {} offer applied successfully on the Product {}", coupon.getCode(), coupon.getProduct());
            return new ResponseDto<>(Status.SUCCESS.getStatusCode().value(), Status.SUCCESS.getStatusDescription(), requestId, "Coupon applied successfully", billResponse);
        }
        logger.error("The conditions of the coupon {} are not full filled", coupon.getCode());
        return new ResponseDto<>(Status.VALIDATION_FAILED.getStatusCode().value(), Status.VALIDATION_FAILED.getStatusDescription(), requestId, "The conditions of the coupon are not fullfilled", null);
    }

    @Override
    public ResponseDto<?> removeCoupon(int requestId, Pageable pageable) throws Exception {
        logger.info("Removing the applied coupon on the cart");
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        users u = userService.getByUsername(auth.getName());
        Cart cart = u.getCart();
        if (auth == null || auth.getPrincipal().equals("anonymousUser")) {
            logger.error("No user logged in");
            return new ResponseDto<>(Status.BAD_REQUEST.getStatusCode().value(), Status.BAD_REQUEST.getStatusDescription(), requestId, "User need to logged in to access the cart", null);
        }
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
        billResponse.setItems(billItems);
        billResponse.setSubtotal(subtotal);
        billResponse.setGst(subtotal * 0.18);
        billResponse.setTotal(subtotal + subtotal * 0.18);
        logger.info("Coupon offer removed successfully");
        logger.info("The temporary bill for the cart is generated successfully");
        return new ResponseDto<>(Status.SUCCESS.getStatusCode().value(), Status.SUCCESS.getStatusDescription(), requestId, "Cart bill generated ", billResponse);
    }
}
