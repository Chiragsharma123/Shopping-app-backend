package com.example.imagedemo.impl;

import com.example.imagedemo.common.ResponseDto;
import com.example.imagedemo.common.Status;
import com.example.imagedemo.dto.sellerRequestDto;
import com.example.imagedemo.model.Seller;
import com.example.imagedemo.service.sellerService;
import org.apache.commons.validator.routines.EmailValidator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Component
public class sellerManagerImpl {
    Logger logger = LoggerFactory.getLogger(sellerManagerImpl.class);
    @Autowired
    private sellerService sellerService;
    @Autowired
    private PasswordEncoder passwordEncoder;

    public ResponseDto<?> registerSeller(sellerRequestDto requestDto, int requestId) {
        logger.info("Registering user : {}", requestDto.getName());
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null || requestDto.getId() != 0 || !auth.getPrincipal().equals("anonymousUser")) {
            logger.error("Invalid request need to logout");
            return new ResponseDto<>(Status.BAD_REQUEST.getStatusCode().value(), Status.BAD_REQUEST.getStatusDescription(), requestId, "Invalid request need to logout", null);
        }
        if (requestDto.getName() == null || requestDto.getName().trim().isEmpty()) {
            logger.error("Registration failed : user name is null or empty");
            return new ResponseDto<>(Status.BAD_REQUEST.getStatusCode().value(), Status.BAD_REQUEST.getStatusDescription(), requestId, "User can not be blank", null);
        }
        if (requestDto.getPassword() == null || requestDto.getPassword().length() < 6) {
            logger.error("Registration Failed: Password can't be empty or null");
            return new ResponseDto<>(Status.BAD_REQUEST.getStatusCode().value(), Status.BAD_REQUEST.getStatusDescription(), requestId, "Password must be of at least 6 characters", null);
        }
        if (requestDto.getEmail() == null || requestDto.getEmail().trim().isEmpty()) {
            logger.error("Registration Failed: Email can't be null or empty");
            return new ResponseDto<>(Status.BAD_REQUEST.getStatusCode().value(), Status.BAD_REQUEST.getStatusDescription(), requestId, "Email can't be null or empty", null);
        }
        if (!EmailValidator.getInstance().isValid(requestDto.getEmail())) {
            logger.error("Registration Failed: Invalid email format");
            return new ResponseDto<>(Status.BAD_REQUEST.getStatusCode().value(), Status.BAD_REQUEST.getStatusDescription(), requestId, "Invalid email format", null);
        }
        Seller seller = new Seller();
        seller.setName(requestDto.getName());
        seller.setPassword(passwordEncoder.encode(requestDto.getPassword()));
        seller.setBrandName(requestDto.getBrandName());
        seller.setBrandLogo(requestDto.getImageData());
        seller.setAddress(requestDto.getAddress());
        seller.setEmail(requestDto.getEmail());
        seller.setDelivery_pinCodes(requestDto.getDelivery_pinCodes());
        seller.setPhoneNumber(requestDto.getPhoneNumber());
        seller.setCreatedAt(LocalDateTime.now());
        seller.setUpdatedAt(LocalDateTime.now());
        seller.setStatus("created");
        sellerService.registerSeller(seller);
        logger.info("Seller {} is registered successfully",requestDto.getName());
        return new ResponseDto<>(Status.SUCCESS.getStatusCode().value(),Status.SUCCESS.getStatusDescription(), requestId,"Registration is successful",requestDto.getName());
    }
}
