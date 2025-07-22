package com.example.imagedemo.model;


import com.opencsv.bean.CsvBindByName;
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
    @CsvBindByName
    private String name;
    @CsvBindByName
    private String description;
    @CsvBindByName
    private Long price;
    @CsvBindByName
    private String category;
    @CsvBindByName
    private int quantity;
    @CsvBindByName
    private String Brand;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private String status;
    @Lob
    private byte[] imageData;
    @CsvBindByName
    @Transient
    private String imagePath;
    private long soldCount;
    @ManyToOne
    @JoinColumn(name = "sellerId")
    private Seller seller;
    private String deliveryPinCodes;
    private long costPrice;
    private long returnCount;
}
