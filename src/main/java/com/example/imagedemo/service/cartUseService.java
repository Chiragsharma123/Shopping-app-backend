package com.example.imagedemo.service;

import com.example.imagedemo.model.Cart;
import com.example.imagedemo.repository.cartUserRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class cartUseService {
    @Autowired
    private cartUserRepo cartUserRepo;

    public Cart getSpecificCart(int id) {
        return cartUserRepo.findById(id).orElse(null);
    }

    public void setStatus(Cart c) {
        cartUserRepo.save(c);
    }

    public void setCart(Cart c) {
        cartUserRepo.save(c);
    }
}
