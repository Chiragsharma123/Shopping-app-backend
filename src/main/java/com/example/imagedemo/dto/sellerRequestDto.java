package com.example.imagedemo.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class sellerRequestDto {
    private int id;
    private String name;
    private String email;
    private String password;
    private String phoneNumber;
    private String address;
    private String delivery_pinCodes;
    private String status;
    private String brandName;
    private byte[] imageData;
}
