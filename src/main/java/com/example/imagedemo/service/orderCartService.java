package com.example.imagedemo.service;

import com.example.imagedemo.model.Cart;
import com.example.imagedemo.model.OrderCart;
import com.example.imagedemo.model.Product;
import com.example.imagedemo.repository.orderCartRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class orderCartService {
    @Autowired
    private orderCartRepo orderRepo;

    public void saveOrder(OrderCart order) {
        orderRepo.save(order);
    }

    public Page<OrderCart> getAllOrders(Cart c , Pageable pageable ,String status) {
        return orderRepo.findByCartAndStatus(c , status , pageable);
    }

    public void deleteByCart(Cart c) {
        orderRepo.deleteByCart(c);
    }

    public Page<OrderCart> getOrdersFromPast(Cart c,String status , Pageable pageable, LocalDateTime fromDate) {
        return orderRepo.findByCartAndStatusAndCreatedAtAfter(c,status, pageable , fromDate);
    }
    public OrderCart getOrderById(int orderId) {
        return orderRepo.findById(orderId).orElse(null);
    }

    public List<OrderCart> getAnnualOrders(LocalDateTime startYOfYear, LocalDateTime endOfYear, String invoiced) {
        return orderRepo.findByCreatedAtBetweenAndStatus(startYOfYear,endOfYear,invoiced);
    }
}
