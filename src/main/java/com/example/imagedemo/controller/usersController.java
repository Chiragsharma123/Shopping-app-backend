package com.example.imagedemo.controller;

import com.example.imagedemo.common.ResponseDto;
import com.example.imagedemo.common.Status;
import com.example.imagedemo.dto.loginRequestDto;
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

    @PostMapping("/register")
    public ResponseDto<?> registerUser(@RequestBody users user, @RequestHeader("Request-id") int requestId) {
        try {
            return userManager.UserRegister(user, requestId);
        } catch (Exception e) {
            return new ResponseDto<>(Status.INTERNAL_ERROR.getStatusCode().value(), Status.INTERNAL_ERROR.getStatusDescription(), requestId, e.getMessage(), null);
        }
    }

    @PostMapping("/login")
    public ResponseDto<?> loginUser(@RequestBody loginRequestDto logUser, HttpServletResponse httpServletResponse, @RequestHeader("Request-id") int requestId) {
        try {
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

    @DeleteMapping("/delete/{uid}")
    public ResponseDto<?> deleteUser(@PathVariable int uid, @RequestHeader("Request-id") int requestId) {
        try {
            return userManager.deleteUser(uid, requestId);

        } catch (Exception e) {
            return new ResponseDto<>(Status.INTERNAL_ERROR.getStatusCode().value(), Status.INTERNAL_ERROR.getStatusDescription(), requestId, e.getMessage(), null);
        }
    }

    @PutMapping("/update/{uId}")
    public ResponseDto<?>updateUser(@PathVariable int uId ,@RequestBody users user, @RequestHeader("Request-id")int requestId){
        try{
            return userManager.updateUser(uId ,user, requestId);
        }catch (Exception e) {
            return new ResponseDto<>(Status.INTERNAL_ERROR.getStatusCode().value(), Status.INTERNAL_ERROR.getStatusDescription(), requestId, e.getMessage(), null);
        }
    }
}