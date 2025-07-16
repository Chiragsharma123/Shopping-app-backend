package com.example.imagedemo.util;

import com.example.imagedemo.common.ResponseDto;
import com.example.imagedemo.dto.OrderDto;
import org.springframework.data.domain.Pageable;

public interface OrderValidation {
    ResponseDto<?> placeOrder(int requestId, OrderDto requestDto ) throws Exception;

    ResponseDto<?> getAllOrders(int requestId, Pageable pageable) throws Exception;

    ResponseDto<?> getOrderByCustomDate(int requestId, OrderDto request) throws Exception;

    ResponseDto<?> getAllProductsOfOrder(int requestId, OrderDto request) throws Exception;

    ResponseDto<?> returnProductFromOrder(int requestId, OrderDto request) throws Exception;

    ResponseDto<?> placeOrderWithCoupon(int requestId, OrderDto request)throws Exception;
}
