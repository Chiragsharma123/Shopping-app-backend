package com.example.imagedemo.controller;

import com.example.imagedemo.common.ResponseDto;
import com.example.imagedemo.common.Status;
import com.example.imagedemo.dto.billResponseDto;
import com.example.imagedemo.impl.OrderManagerImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/order")
public class orderController {
    @Autowired
    private OrderManagerImpl orderManager;

    @PostMapping("/placed")
    public ResponseDto<?> placeOrder(@RequestHeader("Request-id") int requestId, @RequestParam int page, @RequestParam int size) {
        try {
            Pageable pageable = PageRequest.of(page, size);
            return orderManager.placeOrder(requestId, pageable );
        } catch (Exception e) {
            return new ResponseDto<>(Status.INTERNAL_ERROR.getStatusCode().value(), Status.INTERNAL_ERROR.getStatusDescription(), requestId, e.getMessage(), null);
        }
    }

    @PostMapping("/placed/coupon/{cId}")
    public ResponseDto<?>placeOrderWithCoupon(@RequestHeader("Request-id") int requestId, @RequestParam int page, @RequestParam int size , @PathVariable int cId){
        try {
            Pageable pageable = PageRequest.of(page, size);
            return orderManager.placeOrderWithCoupon(requestId, pageable , cId);
        } catch (Exception e) {
            return new ResponseDto<>(Status.INTERNAL_ERROR.getStatusCode().value(), Status.INTERNAL_ERROR.getStatusDescription(), requestId, e.getMessage(), null);
        }
    }

    @GetMapping("/orders")
    public ResponseDto<?> getAllOrders(@RequestHeader("Request-id") int requestId, @RequestParam int page, @RequestParam int size) {
        try {
            Pageable pageable = PageRequest.of(page, size);
            return orderManager.getAllOrders(requestId, pageable);
        } catch (Exception e) {
            return new ResponseDto<>(Status.INTERNAL_ERROR.getStatusCode().value(), Status.INTERNAL_ERROR.getStatusDescription(), requestId, e.getMessage(), null);
        }
    }

    @GetMapping("/history")
    public ResponseDto<?> getOrdersByCustomDate(@RequestHeader("Request-id") int requestId, @RequestParam long duration, @RequestParam(defaultValue = "MONTHS") String unit, @RequestParam int page, @RequestParam int size) {
        try {
            Pageable pageable = PageRequest.of(page, size);
            return orderManager.getOrderByCustomDate(requestId, duration, unit, pageable);
        } catch (Exception e) {
            return new ResponseDto<>(Status.INTERNAL_ERROR.getStatusCode().value(), Status.INTERNAL_ERROR.getStatusDescription(), requestId, e.getMessage(), null);
        }
    }

    @GetMapping("/ProductList/{orderId}")
    public ResponseDto<?> getAllProductOfOrder(@RequestHeader("Request-id") int requestId, @RequestParam int page, @RequestParam int size, @PathVariable int orderId) {
        try {
            Pageable pageable = PageRequest.of(page, size);
            return orderManager.getAllProductsOfOrder(requestId, pageable, orderId);
        } catch (Exception e) {
            return new ResponseDto<>(Status.INTERNAL_ERROR.getStatusCode().value(), Status.INTERNAL_ERROR.getStatusDescription(), requestId, e.getMessage(), null);
        }
    }

    @PostMapping("/{orderId}/Product/{pId}")
    public ResponseDto<?> returnProductFromOrder(@RequestHeader("Request-id") int requestId, @PathVariable int orderId, @PathVariable int pId,@RequestParam int quantity) {
        try {
            return orderManager.returnProductFromOrder(requestId, orderId, pId , quantity);
        } catch (Exception e) {
            return new ResponseDto<>(Status.INTERNAL_ERROR.getStatusCode().value(), Status.INTERNAL_ERROR.getStatusDescription(), requestId, e.getMessage(), null);
        }
    }
}
