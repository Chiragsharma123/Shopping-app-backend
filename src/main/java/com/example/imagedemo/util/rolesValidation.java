package com.example.imagedemo.util;

import com.example.imagedemo.common.ResponseDto;
import com.example.imagedemo.dto.rolesDto;

public interface rolesValidation {
    ResponseDto<?> addRole(rolesDto requestDto, int requestId);

    ResponseDto<?> deleteRole(rolesDto requestDto, int requestId);
}
