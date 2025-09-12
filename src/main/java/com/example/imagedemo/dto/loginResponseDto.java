package com.example.imagedemo.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class loginResponseDto {
    private String username;
    private String role;
    private String token;
}
