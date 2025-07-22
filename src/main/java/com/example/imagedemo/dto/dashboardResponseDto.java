package com.example.imagedemo.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class dashboardResponseDto {
    private double TotalRevenue;
    private long TotalProfile;
    private List<String>HighestSellingProducts;
    private List<String>LeastSellingProducts;
    private double CurrentMonthSale;
    private double Profit ;
    private double Loss;
    private List<String>MaximumReturningProducts;
}
