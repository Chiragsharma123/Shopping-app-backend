package com.example.imagedemo.service;

import com.example.imagedemo.model.Product;
import com.example.imagedemo.repository.productRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class productService {

    @Autowired
    private productRepo pRepo;

    public void addProduct(Product p) {
        pRepo.save(p);
    }

    public Page<Product> getallproduct(Pageable pageable) {
        return pRepo.findAll(pageable);
    }

    public Product getSpecificProduct(int pId) {
        return pRepo.findById(pId).orElse(null);
    }

    public Page<Product> getByCategory(String category , Pageable pageable) {
        return pRepo.findByCategory(category , pageable);
    }

    public List<Product> getHighestSellingProducts() {
        return pRepo.findTop5ByOrderBySoldCountDesc();
    }

    public List<Product> getLowestSellingProducts() {
        return pRepo.findTop5ByOrderBySoldCountAsc();
    }
}
