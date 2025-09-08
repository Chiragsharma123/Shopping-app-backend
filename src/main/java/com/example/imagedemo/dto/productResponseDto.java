package com.example.imagedemo.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class productResponseDto {
    private int id;
    private String imageBase64;
    private String name;
    private long price;
    private String description;
    private String category;
    private int quantity;
}
