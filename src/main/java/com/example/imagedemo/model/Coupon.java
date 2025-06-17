package com.example.imagedemo.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Coupon {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int cId;

    private String code;
    private int count;
    private String discountUnit;
    private long discountValue;
    private String description;
    private String category;
    private String status;
    private long offerAvailableOn;
    private LocalDateTime expiresAt;

    @ManyToOne
    @JoinColumn(name = "ProductId")
    private Product product;
}
