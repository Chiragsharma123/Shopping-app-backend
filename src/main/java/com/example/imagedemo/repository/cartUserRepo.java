package com.example.imagedemo.repository;

import com.example.imagedemo.model.Cart;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface cartUserRepo extends JpaRepository<Cart, Integer> {
}
