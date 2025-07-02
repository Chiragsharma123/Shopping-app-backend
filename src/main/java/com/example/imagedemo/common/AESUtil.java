package com.example.imagedemo.common;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;
import java.util.Base64;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Component
public class AESUtil {

    @Value("${encryption.secret-key}")
    private String secretKey;

    public String encryptJson(String plainText) throws Exception {
        SecretKeySpec key = new SecretKeySpec(secretKey.getBytes(), "AES");
        Cipher cipher = Cipher.getInstance("AES");
        cipher.init(Cipher.ENCRYPT_MODE, key);
        return Base64.getEncoder().encodeToString(cipher.doFinal(plainText.getBytes()));
    }

    public String decrypt(String base64) throws Exception {
        SecretKeySpec key = new SecretKeySpec(secretKey.getBytes(), "AES");
        Cipher cipher = Cipher.getInstance("AES");
        cipher.init(Cipher.DECRYPT_MODE, key);
        return new String(cipher.doFinal(Base64.getDecoder().decode(base64)));
    }

    public <T> T decryptToObject(String encryptedPayload, Class<T> targetType) throws Exception {
        String json = decrypt(encryptedPayload);
        return new ObjectMapper().readValue(json, targetType);
    }
}
