package com.example.imagedemo.controller;

import com.example.imagedemo.common.ResponseDto;
import com.example.imagedemo.common.Status;
import com.example.imagedemo.dto.PagingDto;
import com.example.imagedemo.dto.productRequestDto;
import com.example.imagedemo.dto.productResponseDto;
import com.example.imagedemo.impl.ProductsManagerImpl;
import com.example.imagedemo.model.Product;
import com.example.imagedemo.service.productService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequestMapping("/products")
public class productController {
    @Autowired
    private productService pService;
    @Autowired
    private ProductsManagerImpl productsManager;

    @PostMapping("/upload")
    public ResponseDto<?> addProduct(@RequestBody List<productRequestDto> p, @RequestHeader("Request-id") int requestId) {
        try {
            return productsManager.addProduct(p, requestId);
        } catch (Exception e) {
            return new ResponseDto<>(Status.INTERNAL_ERROR.getStatusCode().value(), Status.INTERNAL_ERROR.getStatusDescription(), requestId, e.getMessage(), null);
        }
    }

    @PostMapping("/UploadMultiple")
    public ResponseDto<?> addMultipleProduct(@RequestParam("file") MultipartFile file, @RequestHeader("Request-id") int requestId) {
        try {
            return productsManager.addMulitpleProduct(file, requestId);
        } catch (Exception e) {
            return new ResponseDto<>(Status.INTERNAL_ERROR.getStatusCode().value(), Status.INTERNAL_ERROR.getStatusDescription(), requestId, e.getMessage(), null);
        }
    }

    @GetMapping("/products")
    public ResponseDto<?> getAllProducts(@RequestHeader("Request-id") int requestId,  @RequestParam(defaultValue = "0") int page,
                                         @RequestParam(defaultValue = "10") int size ){
        try {

            Pageable pageable = PageRequest.of(page, size);
            return productsManager.getAllProducts(requestId, pageable);
        } catch (Exception e) {
            return new ResponseDto<>(Status.INTERNAL_ERROR.getStatusCode().value(), Status.INTERNAL_ERROR.getStatusDescription(), requestId, e.getMessage(), null);
        }
    }

    @GetMapping("/{p_id}")
    public ResponseDto<?>getProductDetails(@RequestHeader("Request-id") int requestId, @PathVariable Integer p_id){
        try{
            return  productsManager.getProductDetails(requestId , p_id);
        }catch (Exception e) {
            return new ResponseDto<>(Status.INTERNAL_ERROR.getStatusCode().value(), Status.INTERNAL_ERROR.getStatusDescription(), requestId, e.getMessage(), null);
        }
    }

    @DeleteMapping("/delete")
    public ResponseDto<?> deleteProduct(@RequestBody productRequestDto request, @RequestHeader("Request-id") int requestId) {
        try {
            return (productsManager.deleteProduct(request.getProductId(), requestId));
        } catch (Exception e) {
            return new ResponseDto<>(Status.INTERNAL_ERROR.getStatusCode().value(), Status.INTERNAL_ERROR.getStatusDescription(), requestId, e.getMessage(), null);
        }
    }

    @GetMapping("/category")
    public ResponseDto<?> fetchByCategory(@RequestBody productRequestDto request, @RequestHeader("Request-id") int requestId) {
        try {
            return productsManager.fetchByCategory(request, requestId);
        } catch (Exception e) {
            return new ResponseDto<>(Status.INTERNAL_ERROR.getStatusCode().value(), Status.INTERNAL_ERROR.getStatusDescription(), requestId, e.getMessage(), null);
        }
    }

    @PatchMapping("/updateQuantity")
    public ResponseDto<?> updateQuantity(@RequestBody productRequestDto request, @RequestHeader("Request-id") int requestId) {
        try {
            return productsManager.updateQuantity(request.getProductId(), request.getQuantity(), requestId);
        } catch (Exception e) {
            return new ResponseDto<>(Status.INTERNAL_ERROR.getStatusCode().value(), Status.INTERNAL_ERROR.getStatusDescription(), requestId, e.getMessage(), null);
        }
    }
}
