package com.example.imagedemo.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class activeCouponResponseDto {
    private int id;
    private String code;
    private String description;
    private LocalDateTime expiresAt;
}
