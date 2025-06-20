package com.example.imagedemo.impl;

import com.example.imagedemo.common.ResponseDto;
import com.example.imagedemo.common.Status;
import com.example.imagedemo.dto.productRequestDto;
import com.example.imagedemo.dto.productResponseDto;
import com.example.imagedemo.util.ProductsValidation;
import com.example.imagedemo.model.Product;
import com.example.imagedemo.service.productService;
import com.opencsv.bean.CsvToBeanBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Component
public class ProductsManagerImpl implements ProductsValidation {
    Logger logger = LoggerFactory.getLogger(ProductsManagerImpl.class);
    @Autowired
    private productService productService;

    @Override
    public ResponseDto<?> addProduct(List<productRequestDto> p, int requestId) throws Exception {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (p == null) {
            logger.error("Incomplete data");
            return new ResponseDto<>(Status.BAD_REQUEST.getStatusCode().value(), Status.BAD_REQUEST.getStatusDescription(), requestId, "Please Provide the complete data", null);
        }
        List<String>productNames=new ArrayList<>();
       for(productRequestDto dto:p){
           Product product=new Product();
           product.setName(dto.getName());
           product.setDescription(dto.getDescription());
           product.setQuantity(dto.getQuantity());
           product.setCategory(dto.getCategory());
           product.setUpdatedAt(LocalDateTime.now());
           product.setCreatedAt(LocalDateTime.now());
           product.setPrice((long) dto.getPrice());
           product.setBrand(dto.getBrand());
           if(dto.getQuantity()>0){
               product.setStatus("Available");
           }
           else{
               product.setStatus("Unavailable");
           }
           product.setImageData(dto.getImageData());
           productNames.add(dto.getName());
           productService.addProduct(product);
       }
       logger.info("All products are added successfully");
        return new ResponseDto<>(Status.CREATED.getStatusCode().value(), Status.CREATED.getStatusDescription(), requestId, "Product Uploaded to website successfully", productNames );
    }

    @Override
    public ResponseDto<?> getAllProducts(int requestId , Pageable pageable) throws Exception {
        logger.info("Fetching all the products");
       Page<Product> response = productService.getallproduct(pageable);
        List<productResponseDto> ProductList = new ArrayList<>();
        if (!response.isEmpty()) {
            for (Product p : response) {
                productResponseDto items = new productResponseDto();
                items.setName(p.getName());
                items.setImageBase64(Arrays.toString(p.getImageData()));
                items.setPrice(p.getPrice());
                items.setDescription(p.getDescription());
                items.setCategory(p.getCategory());
                items.setQuantity(p.getQuantity());
                ProductList.add(items);
            }
            logger.info("All the products are fetched ");
            return new ResponseDto<>(Status.SUCCESS.getStatusCode().value(), Status.SUCCESS.getStatusDescription(), requestId, "All Products are Fetched Successfully", ProductList);
        }
        logger.info("There is no product to show");
        return new ResponseDto<>(Status.BAD_REQUEST.getStatusCode().value(), Status.BAD_REQUEST.getStatusDescription(), requestId, "There is no product to show", null);
    }

    @Override
    public ResponseDto<?> updateProduct(int p_id, Product p, MultipartFile image, int requestid) throws Exception {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if ((p == null) || (image == null)) {
            logger.error("Incomplete data");
            return new ResponseDto<>(Status.BAD_REQUEST.getStatusCode().value(), Status.BAD_REQUEST.getStatusDescription(), requestid, "Please Provide the complete Data", null);
        }
        Product p1 = productService.getSpecificProduct(p_id);
        if (p1 != null) {
            p1.setImageData(image.getBytes());
            p1.setName(p.getName());
            p1.setDescription(p.getDescription());
            p1.setPrice(p.getPrice());
            p1.setCategory(p.getCategory());
            p1.setCreatedAt(p.getCreatedAt());
            p1.setUpdatedAt(LocalDateTime.now());
            if(p.getQuantity() + p1.getQuantity()>0){
                p1.setStatus("Available");
            }else{
                p1.setStatus("Unavailable");
            }
            productService.addProduct(p1);
            logger.info("Product {} is updated successfully", p.getName());
            return new ResponseDto<>(Status.UPDATED.getStatusCode().value(), Status.UPDATED.getStatusDescription(), requestid, "Product is Updated Successfully", p1);
        }
        logger.error("Product is not present in the database");
        return new ResponseDto<>(Status.NOT_FOUND.getStatusCode().value(), Status.NOT_FOUND.getStatusDescription(), requestid, "Product is not present in the Database", null);
    }

    @Override
    public ResponseDto<?> deleteProduct(int pId, int requestId) throws Exception {
        logger.info("Deleting a Product");
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        Product p = productService.getSpecificProduct(pId);
        if (p != null) {
            logger.info("Product {} is deleted successfully", p.getName());
            return new ResponseDto<>(Status.DELETED.getStatusCode().value(), Status.DELETED.getStatusDescription(), requestId, "Product is deleted successfully", p);
        }
        logger.error("Product {} not Exists", p.getName());
        return new ResponseDto<>(Status.BAD_REQUEST.getStatusCode().value(), Status.BAD_REQUEST.getStatusDescription(), requestId, "Product doesn't exists in the database", null);
    }

    @Override
    public ResponseDto<?> fetchByCategory(String category, int requestId , Pageable pageable) throws Exception {
        logger.info("Fetching all products of {}", category);
        Page<Product> response = productService.getByCategory(category , pageable);
        if (response.isEmpty()) {
            logger.info("No product of {} category is present on the website", category);
            return new ResponseDto<>(Status.NOT_FOUND.getStatusCode().value(), Status.NOT_FOUND.getStatusDescription(), requestId, "No product of the requested category is present in the database", null);
        }
        List<productResponseDto> ProductList = new ArrayList<>();
        for (Product p : response) {
            productResponseDto dto = new productResponseDto();
            dto.setName(p.getName());
            dto.setImageBase64(Arrays.toString(p.getImageData()));
            dto.setPrice(p.getPrice());
            dto.setDescription(p.getDescription());
            dto.setCategory(p.getCategory());
            dto.setQuantity(p.getQuantity());
            ProductList.add(dto);
        }
        logger.info(" All Products of {} category are fetched ", category);
        return new ResponseDto<>(Status.SUCCESS.getStatusCode().value(), Status.SUCCESS.getStatusDescription(), requestId, "All Products of the request category are fetched successfully", ProductList);
    }

    @Override
    public ResponseDto<?> updateQuantity(int pId, int quantity, int requestId) throws Exception {
        logger.info("Updating the quantity of the product");
        Product p = productService.getSpecificProduct(pId);
        if (p != null) {
            p.setQuantity(quantity);
            p.setUpdatedAt(LocalDateTime.now());
            if(quantity>0){
                p.setStatus("Available");
            }
            productService.addProduct(p);
            logger.info("Quantity of product {} is updated to {}", p.getName(), quantity);
            return new ResponseDto<>(Status.UPDATED.getStatusCode().value(), Status.UPDATED.getStatusDescription(), requestId, "Quantity of the product is updated successfully", p);
        }
        logger.error("Product doesn't found in the database");
        return new ResponseDto<>(Status.NOT_FOUND.getStatusCode().value(), Status.NOT_FOUND.getStatusDescription(), requestId, "Product doesn't found in the database", null);
    }

    @Override
    public ResponseDto<?> addMulitpleProduct(MultipartFile file,int requestId) throws Exception {
        logger.info("Adding multiple products");
        List<Product> products = new CsvToBeanBuilder<Product>(new InputStreamReader(file.getInputStream()))
                .withType(Product.class)
                .withIgnoreLeadingWhiteSpace(true)
                .build()
                .parse();
        List<String>pName= new ArrayList<>();
        for(Product p:products){
            p.setCreatedAt(LocalDateTime.now());
            p.setUpdatedAt(LocalDateTime.now());
            p.setStatus("Available");
            String imagePath = p.getImagePath() != null ? p.getImagePath().trim() : null;
            if (p.getImagePath() != null && !p.getImagePath().isBlank()) {
                try {
                    byte[] imageBytes = Files.readAllBytes(Paths.get(p.getImagePath()));
                    p.setImageData(imageBytes);
                } catch (IOException e) {
                    logger.error("Error reading image for product: " + p.getName(), e);
                    continue;
                }
            }
            productService.addProduct(p);
            pName.add(p.getName());
        }
        logger.info("All the products from the csv file are added successfully");
        return new ResponseDto<>(Status.CREATED.getStatusCode().value(),Status.CREATED.getStatusDescription(), requestId,"All product added",pName);
    }
}
