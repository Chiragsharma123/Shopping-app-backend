package com.example.imagedemo.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
public class OrderCart {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int orderId;

    @ManyToOne
    @JoinColumn(name = "cartId", nullable = false)
    private Cart cart;

    @ManyToOne
    @JoinColumn(name = "couponId")
    private Coupon coupon;

    private String status;

    private double totalPrice;
    private double refundedAmount=0;
    private double finalAmount=0;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private double discountGivenInRs;
}
