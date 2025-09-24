package com.example.imagedemo.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class couponRequestDto {
    private int couponId;

//    public void setCId(int cId) {
//        this.cId = cId;
//    }

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
    private LocalDateTime expiresAt;
    private PagingDto pagingDto;
}
