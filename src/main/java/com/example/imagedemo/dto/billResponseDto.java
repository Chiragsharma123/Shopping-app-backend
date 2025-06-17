package com.example.imagedemo.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class billResponseDto {
    private List<ProductBillItems> items;
    private double discountedAmount;
    private double AfterDiscountAmount;
    private double subtotal;
    private double gst;
    private double Total;

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public static class ProductBillItems {
        private String name;
        private int quantity;
        private double price;
        private double discount;
        private double total;
    }
}
