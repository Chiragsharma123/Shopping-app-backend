package com.example.imagedemo.controller;

import com.example.imagedemo.common.ResponseDto;
import com.example.imagedemo.common.Status;
import com.example.imagedemo.dto.sellerRequestDto;
import com.example.imagedemo.impl.sellerManagerImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/seller")
public class sellerController {
    @Autowired
    private sellerManagerImpl sellerManager;
    @PostMapping("/register")
    public ResponseDto<?>registerSeller(@RequestBody sellerRequestDto requestDto, @RequestHeader("Request-id") int requestId){
        try{
            return sellerManager.registerSeller(requestDto,requestId);
        }catch (Exception e) {
            return new ResponseDto<>(Status.INTERNAL_ERROR.getStatusCode().value(), Status.INTERNAL_ERROR.getStatusDescription(), requestId, e.getMessage(), null);
        }
    }
}
