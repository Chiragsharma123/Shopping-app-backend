package com.example.imagedemo.util;

import com.example.imagedemo.common.ResponseDto;
import com.example.imagedemo.dto.productRequestDto;
import com.example.imagedemo.model.Product;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface ProductsValidation {
    ResponseDto<?> addProduct(List<productRequestDto> p, int requestId) throws Exception;

    ResponseDto<?> getAllProducts(int requestId , Pageable pageable) throws Exception;

    ResponseDto<?> updateProduct(int p_id, Product p, MultipartFile image, int requestId) throws Exception;

    ResponseDto<?> deleteProduct(int pId, int requestId) throws Exception;

    ResponseDto<?> fetchByCategory(String category, int requestId , Pageable pageable) throws Exception;

    ResponseDto<?> updateQuantity(int pId, int quantity, int requestId) throws Exception;

    ResponseDto<?> addMulitpleProduct(MultipartFile file , int requestId)throws Exception;
}
