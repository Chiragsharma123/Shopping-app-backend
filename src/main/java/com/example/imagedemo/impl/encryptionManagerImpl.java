package com.example.imagedemo.impl;

import com.example.imagedemo.common.AESUtil;
import com.example.imagedemo.dto.EncryptedDto;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
public class encryptionManagerImpl {
    @Autowired
    private AESUtil aesUtil;
    @Autowired
    private ObjectMapper objectMapper;
    public EncryptedDto encrypt(Map<String, Object> jsonMap) throws Exception {
        String json = objectMapper.writeValueAsString(jsonMap);
        String encrypted = aesUtil.encryptJson(json);
        EncryptedDto encryptedDto = new EncryptedDto();
        encryptedDto.setPayload(encrypted);
        return encryptedDto;
    }
}
