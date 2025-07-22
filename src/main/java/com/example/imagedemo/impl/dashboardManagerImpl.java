package com.example.imagedemo.impl;

import com.example.imagedemo.common.ResponseDto;
import com.example.imagedemo.common.Status;
import com.example.imagedemo.dto.dashboardRequestDto;
import com.example.imagedemo.dto.dashboardResponseDto;
import com.example.imagedemo.model.*;
import com.example.imagedemo.service.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

@Component
public class dashboardManagerImpl {
    Logger logger = LoggerFactory.getLogger(dashboardManagerImpl.class);
    @Autowired
    private orderCartService orderService;
    @Autowired
    private userService userService;
    @Autowired
    private productService productService;
    @Autowired
    private sellerService sellerService;
    @Autowired
    private cartOrderProductService cartOrderProductService;

    public ResponseDto<?> FetchAllData(dashboardRequestDto requestDto, int requestId) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String role = auth.getAuthorities().stream()
                .findFirst()
                .map(grantedAuthority -> grantedAuthority.getAuthority())
                .orElse("ROLE_ADMIN");
        if("ROLE_SELLER".equalsIgnoreCase(role)){
            Seller seller = sellerService.findByEmail(auth.getName());
            logger.info("Seller {} is trying to fetch the dashboard data",seller.getName());
            if(requestDto.getUnit()==null || requestDto.getDuration()==0){
                logger.error("please provide the complete data");
                return new ResponseDto<>(Status.BAD_REQUEST.getStatusCode().value(),Status.BAD_REQUEST.getStatusDescription(), requestId,"Please make a valid request",null);
            }
            String TimeUnit = requestDto.getUnit().toUpperCase();
            ChronoUnit chronoUnit = null;
            chronoUnit = ChronoUnit.valueOf(TimeUnit);
            LocalDateTime fromDate = LocalDateTime.now().minus(requestDto.getDuration(), chronoUnit);
            List<Product>HighestSellingProduct = productService.getHighestSellingProductOfSeller(seller.getId());
            List<Product>MaximumReturningProduct = productService.getMaximumReturnedProduct(seller.getId());
            List<String>Statuses = Arrays.asList("Placed" , "Invoiced");
            List<CartOrderProductList>cartItems = cartOrderProductService.getAllItems(seller.getId() , Statuses , fromDate);
            double monthSale = 0;
            double profit  =0;
            double loss =0;
            for(CartOrderProductList items: cartItems){
                monthSale+=items.getProduct().getPrice()*items.getQuantity();
                profit += (items.getProduct().getPrice() - items.getProduct().getCostPrice())* items.getQuantity();
            }
            if(profit<0){
                loss=profit;
                profit=0;
            }
            List<String> HighestSellingName = new ArrayList<>();
            List<String> MaximumReturningName = new ArrayList<>();
            for(Product p:HighestSellingProduct){
                HighestSellingName.add(p.getName());
            }
            for(Product p:MaximumReturningProduct){
                MaximumReturningName.add(p.getName());
            }
            dashboardResponseDto dashboardResponseDto = new dashboardResponseDto();
            dashboardResponseDto.setLoss(loss);
            dashboardResponseDto.setProfit(profit);
            dashboardResponseDto.setCurrentMonthSale(monthSale);
            dashboardResponseDto.setHighestSellingProducts(HighestSellingName);
            dashboardResponseDto.setMaximumReturningProducts(MaximumReturningName);
            logger.info("All the data for the seller is fetched successfully");
          return new ResponseDto<>(Status.SUCCESS.getStatusCode().value(),Status.SUCCESS.getStatusDescription(), requestId,"Dashboard data is fetched successfully for a seller",dashboardResponseDto);
        }
        logger.info("Admin is trying to fetch the dashboard data");
        int year = requestDto.getYear();
        if(year==0){
            logger.error("Please make a valid request");
            return new ResponseDto<>(Status.BAD_REQUEST.getStatusCode().value(),Status.BAD_REQUEST.getStatusDescription(), requestId,"Invalid request",null);
        }
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
        logger.info("All the dashboard for the admin is fetched successfully");
        return new ResponseDto<>(Status.SUCCESS.getStatusCode().value(), Status.SUCCESS.getStatusDescription(), requestId, "Dashboard data fetched successfully", dashboardResponseDto);
    }
}
