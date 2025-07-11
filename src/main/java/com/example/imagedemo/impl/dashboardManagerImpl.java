package com.example.imagedemo.impl;

import com.example.imagedemo.common.ResponseDto;
import com.example.imagedemo.common.Status;
import com.example.imagedemo.dto.dashboardRequestDto;
import com.example.imagedemo.dto.dashboardResponseDto;
import com.example.imagedemo.model.OrderCart;
import com.example.imagedemo.model.Product;
import com.example.imagedemo.model.users;
import com.example.imagedemo.service.orderCartService;
import com.example.imagedemo.service.productService;
import com.example.imagedemo.service.userService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Component
public class dashboardManagerImpl {
    @Autowired
    private orderCartService orderService;
    @Autowired
    private userService userService;
    @Autowired
    private productService productService;

    public ResponseDto<?> FetchAllData(dashboardRequestDto requestDto, int requestId) {
        int year = requestDto.getYear();
        LocalDateTime startYOfYear = LocalDateTime.of(year, 1, 1, 0, 0);
        LocalDateTime endOfYear = startYOfYear.plusYears(1);
        List<OrderCart> ordersInvoiced = orderService.getAnnualOrders(startYOfYear, endOfYear, "Invoiced");
        List<OrderCart> ordersPlaced = orderService.getAnnualOrders(startYOfYear, endOfYear, "Placed");
        double totalRevenue = 0;
        for (OrderCart x : ordersPlaced) {
            totalRevenue += x.getFinalAmount();
        }
        for (OrderCart y : ordersInvoiced) {
            totalRevenue += y.getFinalAmount();
        }
        List<users> userList = userService.getAllUsers();
        long count = 0;
        for (users u : userList) {
            count += 1;
        }
        List<Product> HighestSelling = productService.getHighestSellingProducts();
        List<Product> LeastSelling = productService.getLowestSellingProducts();
        List<String> HighestSellingName = new ArrayList<>();
        List<String> LeastSellingName = new ArrayList<>();
        dashboardResponseDto dashboardResponseDto = new dashboardResponseDto();
        for (Product p : HighestSelling) {
            HighestSellingName.add(p.getName());
        }
        for (Product p : LeastSelling) {
            LeastSellingName.add(p.getName());
        }
        dashboardResponseDto.setHighestSellingProducts(HighestSellingName);
        dashboardResponseDto.setLeastSellingProducts(LeastSellingName);
        dashboardResponseDto.setTotalRevenue(totalRevenue);
        dashboardResponseDto.setTotalProfile(count);
        return new ResponseDto<>(Status.SUCCESS.getStatusCode().value(), Status.SUCCESS.getStatusDescription(), requestId, "Dashboard data fetched successfully", dashboardResponseDto);
    }
}
