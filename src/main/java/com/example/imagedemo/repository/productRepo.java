package com.example.imagedemo.repository;

import com.example.imagedemo.model.Product;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface productRepo extends JpaRepository<Product, Integer> {
    Page<Product> findByCategory(String category , Pageable pageable);
    Page<Product> findAll(Pageable pageable);
}
