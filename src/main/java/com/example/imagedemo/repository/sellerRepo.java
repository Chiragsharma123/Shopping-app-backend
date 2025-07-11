package com.example.imagedemo.repository;

import com.example.imagedemo.model.Seller;
import jakarta.persistence.criteria.CriteriaBuilder;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface sellerRepo extends JpaRepository<Seller , Integer> {
}
