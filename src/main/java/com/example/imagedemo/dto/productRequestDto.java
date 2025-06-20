package com.example.imagedemo.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class productRequestDto {
        private String name;
        private String description;
        private double price;
        private String category;
        private int quantity;
        private String brand;
        private byte[] imageData;
}
