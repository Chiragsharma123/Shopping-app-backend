package com.example.imagedemo.model;


import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.List;

@Data
@Entity
@NoArgsConstructor
@AllArgsConstructor
public class Product {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int pId;
    private String name;
    private String description;
    private Long price;
    private String category;
    private int quantity;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private String status;
    private String Brand;

    @Lob
    private byte[] imageData;

}
