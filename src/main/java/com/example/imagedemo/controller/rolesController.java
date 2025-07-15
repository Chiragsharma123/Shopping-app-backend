package com.example.imagedemo.controller;

import com.example.imagedemo.common.ResponseDto;
import com.example.imagedemo.common.Status;
import com.example.imagedemo.dto.rolesDto;
import com.example.imagedemo.impl.rolesManagerImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/roles")
public class rolesController {
    @Autowired
    private rolesManagerImpl rolesManager;
    @PostMapping("/register")
    public ResponseDto<?> addRoles(@RequestHeader("Request-id") int requestId , @RequestBody rolesDto requestDto){
        try{
           return rolesManager.addRole(requestDto,requestId);
        } catch (Exception e) {
            return new ResponseDto<>(Status.INTERNAL_ERROR.getStatusCode().value(), Status.INTERNAL_ERROR.getStatusDescription(), requestId, e.getMessage(), null);
        }
    }
    @DeleteMapping("/delete")
    public ResponseDto<?> deleteRole(@RequestHeader("Request-id") int requestId , @RequestBody rolesDto requestDto){
        try{
            return rolesManager.deleteRole(requestDto,requestId);
        } catch (Exception e) {
            return new ResponseDto<>(Status.INTERNAL_ERROR.getStatusCode().value(), Status.INTERNAL_ERROR.getStatusDescription(), requestId, e.getMessage(), null);
        }
    }
}
