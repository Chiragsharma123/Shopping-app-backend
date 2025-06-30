package com.example.imagedemo.controller;

import com.example.imagedemo.common.ResponseDto;
import com.example.imagedemo.common.Status;
import com.example.imagedemo.dto.OrderDto;
import com.example.imagedemo.dto.PagingDto;
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
    public ResponseDto<?> placeOrder(@RequestHeader("Request-id") int requestId, @RequestBody PagingDto P) {
        try {
            int page = P.getPage();
            int size =P.getSize();
            Pageable pageable = PageRequest.of(page, size);
            return orderManager.placeOrder(requestId, pageable );
        } catch (Exception e) {
            return new ResponseDto<>(Status.INTERNAL_ERROR.getStatusCode().value(), Status.INTERNAL_ERROR.getStatusDescription(), requestId, e.getMessage(), null);
        }
    }

    @PostMapping("/placed/coupon")
    public ResponseDto<?>placeOrderWithCoupon(@RequestHeader("Request-id") int requestId, @RequestBody OrderDto request){
        try {
            return orderManager.placeOrderWithCoupon(requestId, request );
        } catch (Exception e) {
            return new ResponseDto<>(Status.INTERNAL_ERROR.getStatusCode().value(), Status.INTERNAL_ERROR.getStatusDescription(), requestId, e.getMessage(), null);
        }
    }

    @GetMapping("/orders")
    public ResponseDto<?> getAllOrders(@RequestHeader("Request-id") int requestId, @RequestBody PagingDto Paging) {
        try {
            Pageable pageable = PageRequest.of(Paging.getPage(), Paging.getSize());
            return orderManager.getAllOrders(requestId, pageable);
        } catch (Exception e) {
            return new ResponseDto<>(Status.INTERNAL_ERROR.getStatusCode().value(), Status.INTERNAL_ERROR.getStatusDescription(), requestId, e.getMessage(), null);
        }
    }

    @GetMapping("/history")
    public ResponseDto<?> getOrdersByCustomDate(@RequestHeader("Request-id") int requestId, @RequestBody OrderDto request) {
        try {
            return orderManager.getOrderByCustomDate(requestId, request);
        } catch (Exception e) {
            return new ResponseDto<>(Status.INTERNAL_ERROR.getStatusCode().value(), Status.INTERNAL_ERROR.getStatusDescription(), requestId, e.getMessage(), null);
        }
    }

    @GetMapping("/ProductList")
    public ResponseDto<?> getAllProductOfOrder(@RequestHeader("Request-id") int requestId, @RequestBody OrderDto request) {
        try {
            return orderManager.getAllProductsOfOrder(requestId, request);
        } catch (Exception e) {
            return new ResponseDto<>(Status.INTERNAL_ERROR.getStatusCode().value(), Status.INTERNAL_ERROR.getStatusDescription(), requestId, e.getMessage(), null);
        }
    }

    @PostMapping("/Product")
    public ResponseDto<?> returnProductFromOrder(@RequestHeader("Request-id") int requestId, @RequestBody OrderDto request) {
        try {
            return orderManager.returnProductFromOrder(requestId, request);
        } catch (Exception e) {
            return new ResponseDto<>(Status.INTERNAL_ERROR.getStatusCode().value(), Status.INTERNAL_ERROR.getStatusDescription(), requestId, e.getMessage(), null);
        }
    }
}
