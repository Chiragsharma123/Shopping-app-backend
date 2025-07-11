package com.example.imagedemo.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class userDto {
    private int id;
    private String username;
    private String password;
    private String role;
    private String phoneNumber;
    private String Address;
    private String pinCode;
}
