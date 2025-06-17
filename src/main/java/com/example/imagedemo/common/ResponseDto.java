package com.example.imagedemo.common;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ResponseDto<T> {
    private int StatusCode;
    private String StatusDescription;
    private int RequestId;
    private String message;
    private T data;
}
