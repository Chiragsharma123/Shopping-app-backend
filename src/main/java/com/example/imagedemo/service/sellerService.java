package com.example.imagedemo.service;

import com.example.imagedemo.model.Seller;
import com.example.imagedemo.repository.sellerRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class sellerService {
    @Autowired
    private sellerRepo sellerRepo;
    public void registerSeller(Seller seller) {
       sellerRepo.save(seller);
    }
}
