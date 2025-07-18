package com.example.imagedemo.util;

import com.example.imagedemo.common.ResponseDto;
import com.example.imagedemo.dto.productRequestDto;
import org.springframework.data.domain.Pageable;

public interface CartValidation {
    ResponseDto<?> addProductToCart(productRequestDto P, String username, int requestId) throws Exception;

    ResponseDto<?> removeProductFromCart(int pid, int requestId) throws Exception;

    ResponseDto<?> getAllProductsFromCart(int requestId , Pageable pageable) throws Exception;

    ResponseDto<?> updateQuantity(int pId, int quantity, int requestId) throws Exception;
}
