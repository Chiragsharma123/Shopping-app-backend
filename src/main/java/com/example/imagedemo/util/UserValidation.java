package com.example.imagedemo.util;

import com.example.imagedemo.common.ResponseDto;
import com.example.imagedemo.dto.loginRequestDto;
import com.example.imagedemo.dto.userDto;
import com.example.imagedemo.model.users;
import jakarta.servlet.http.HttpServletResponse;

public interface UserValidation {
    ResponseDto<?> UserRegister(userDto user, int requestId) throws Exception;

    ResponseDto<?> LoginUser(loginRequestDto User, HttpServletResponse response, int requestId) throws Exception;

    ResponseDto<?> logOutUser(HttpServletResponse response, int requestId) throws Exception;

    ResponseDto<?> deleteUser(int id, int requestId) throws Exception;

}
