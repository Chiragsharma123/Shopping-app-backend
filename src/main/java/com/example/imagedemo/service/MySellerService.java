package com.example.imagedemo.service;

import com.example.imagedemo.model.Seller;
import com.example.imagedemo.model.sellerPrincipal;
import com.example.imagedemo.repository.sellerRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class MySellerService implements UserDetailsService {
    @Autowired
    private sellerRepo sellerRepo;
    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        Seller seller = sellerRepo.findByEmail(email);
        if (seller == null) {
            throw new UsernameNotFoundException("Seller not found");
        }
        return new sellerPrincipal(seller);
    }
}
