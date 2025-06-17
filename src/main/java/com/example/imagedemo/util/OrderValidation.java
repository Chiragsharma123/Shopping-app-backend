package com.example.imagedemo.util;

import com.example.imagedemo.common.ResponseDto;
import org.springframework.data.domain.Pageable;

public interface OrderValidation {
    ResponseDto<?> placeOrder(int requestId, Pageable pageable ) throws Exception;

    ResponseDto<?> getAllOrders(int requestId, Pageable pageable) throws Exception;

    ResponseDto<?> getOrderByCustomDate(int requestId, long duration, String unit, Pageable pageable) throws Exception;

    ResponseDto<?> getAllProductsOfOrder(int requestId, Pageable pageable, int orderId) throws Exception;

    ResponseDto<?> returnProductFromOrder(int requestId, int orderId, int pId,int quantity) throws Exception;

    ResponseDto<?> placeOrderWithCoupon(int requestId, Pageable pageable, int cId)throws Exception;
}
