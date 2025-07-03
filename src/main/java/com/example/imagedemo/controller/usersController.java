package com.example.imagedemo.controller;

import com.example.imagedemo.common.AESUtil;
import com.example.imagedemo.common.ResponseDto;
import com.example.imagedemo.common.Status;
import com.example.imagedemo.dto.EncryptedDto;
import com.example.imagedemo.dto.loginRequestDto;
import com.example.imagedemo.dto.userDto;
import com.example.imagedemo.impl.UserManagerImpl;
import com.example.imagedemo.model.users;
import com.example.imagedemo.service.userService;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/user")
public class usersController {

    @Autowired
    private userService uService;
    @Autowired
    private UserManagerImpl userManager;
    @Autowired
    private AESUtil aesUtil;

    @PostMapping("/register")
    public ResponseDto<?> registerUser(@RequestBody userDto user, @RequestHeader("Request-id") int requestId) {
        try {
            return userManager.UserRegister(user, requestId);
        } catch (Exception e) {
            return new ResponseDto<>(Status.INTERNAL_ERROR.getStatusCode().value(), Status.INTERNAL_ERROR.getStatusDescription(), requestId, e.getMessage(), null);
        }
    }

    @PostMapping("/login")
    public ResponseDto<?> loginUser(@RequestBody EncryptedDto encryptedDto, HttpServletResponse httpServletResponse, @RequestHeader("Request-id") int requestId) {
        try {
            loginRequestDto logUser = aesUtil.decryptToObject(encryptedDto.getPayload(), loginRequestDto.class);
            return userManager.LoginUser(logUser, httpServletResponse, requestId);
        } catch (Exception e) {
            return new ResponseDto<>(Status.INTERNAL_ERROR.getStatusCode().value(), Status.INTERNAL_ERROR.getStatusDescription(), requestId, e.getMessage(), null);
        }
    }

    @PostMapping("/logout")
    public ResponseDto<?> logoutUser(HttpServletResponse httpServletResponse, @RequestHeader("Request-id") int requestId) {
        try {
            return userManager.logOutUser(httpServletResponse, requestId);
        } catch (Exception e) {
            return new ResponseDto<>(Status.INTERNAL_ERROR.getStatusCode().value(), Status.INTERNAL_ERROR.getStatusDescription(), requestId, e.getMessage(), null);
        }
    }

    @DeleteMapping("/delete")
    public ResponseDto<?> deleteUser(@RequestBody userDto request, @RequestHeader("Request-id") int requestId) {
        try {
            return userManager.deleteUser(request, requestId);
        } catch (Exception e) {
            return new ResponseDto<>(Status.INTERNAL_ERROR.getStatusCode().value(), Status.INTERNAL_ERROR.getStatusDescription(), requestId, e.getMessage(), null);
        }
    }

}