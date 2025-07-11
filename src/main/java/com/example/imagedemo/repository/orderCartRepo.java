package com.example.imagedemo.repository;

import com.example.imagedemo.model.Cart;
import com.example.imagedemo.model.OrderCart;
import com.example.imagedemo.model.Product;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface orderCartRepo extends JpaRepository<OrderCart, Integer> {
    Page<OrderCart> findByCartAndStatus(Cart c,String status , Pageable pageable );

    void deleteByCart(Cart cart);

    Page<OrderCart> findByCartAndStatusAndCreatedAtAfter(Cart c,String status , Pageable pageable, LocalDateTime fromDate);


    List<OrderCart> findByCreatedAtBetweenAndStatus(LocalDateTime startYOfYear, LocalDateTime endOfYear, String invoiced);
}
