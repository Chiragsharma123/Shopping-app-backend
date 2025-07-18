package com.example.imagedemo.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class OrderDto {
    private int orderId;
    private int couponId;
    private int productId;
    private PagingDto pagingDto;
    private long duration;
    private String unit;
    private int quantity;
    private String pinCode;
}
