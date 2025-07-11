package com.example.imagedemo.controller;

import com.example.imagedemo.common.ResponseDto;
import com.example.imagedemo.common.Status;
import com.example.imagedemo.dto.dashboardRequestDto;
import com.example.imagedemo.impl.dashboardManagerImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/dashboard")
public class dashboardController {
    @Autowired
    private dashboardManagerImpl dashboardManager;
    @GetMapping("/")
    public ResponseDto<?> FetchingData(@RequestBody dashboardRequestDto requestDto , @RequestHeader("Request-id") int requestId){
        try {
            return dashboardManager.FetchAllData(requestDto, requestId);
        }catch (Exception e) {
            return new ResponseDto<>(Status.INTERNAL_ERROR.getStatusCode().value(), Status.INTERNAL_ERROR.getStatusDescription(), requestId, e.getMessage(), null);
        }
    }
}
