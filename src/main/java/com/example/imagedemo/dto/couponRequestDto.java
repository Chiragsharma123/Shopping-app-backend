package com.example.imagedemo.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class couponRequestDto {
    private int cId;
    private String code;
    private String category;
    private String description;
    private int count;
    private String discountUnit;
    private long discountValue;
    private long offerAvailableOn;
    private long duration;
    private String unit;
    private int pId;
    private PagingDto pagingDto;
}
