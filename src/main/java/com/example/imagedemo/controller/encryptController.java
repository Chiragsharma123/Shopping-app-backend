package com.example.imagedemo.controller;

import com.example.imagedemo.common.ResponseDto;
import com.example.imagedemo.dto.EncryptedDto;
import com.example.imagedemo.impl.encryptionManagerImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
public class encryptController {
    @Autowired
    private  encryptionManagerImpl encryptionService;
    @PostMapping("/encrypt")
    public EncryptedDto encryptJson(@RequestBody Map<String, Object> jsonMap) throws Exception {
        return encryptionService.encrypt(jsonMap);
    }
}
