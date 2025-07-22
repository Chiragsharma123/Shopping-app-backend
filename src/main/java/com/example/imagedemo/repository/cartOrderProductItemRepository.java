package com.example.imagedemo.repository;

import com.example.imagedemo.model.Cart;
import com.example.imagedemo.model.OrderCart;
import com.example.imagedemo.model.Product;
import com.example.imagedemo.model.CartOrderProductList;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface cartOrderProductItemRepository extends JpaRepository<CartOrderProductList, Integer> {
    Page<CartOrderProductList> findAllByCartAndStatus(Cart cart, Pageable pageable, String status);

    CartOrderProductList findByCartAndProductAndStatus(Cart cart, Product p, String active);

    void deleteAllByCart(Cart cart);

    Page<CartOrderProductList> findByOrder(OrderCart order, Pageable pageable);

    CartOrderProductList findByCartAndProductAndOrder(Cart c, Product p, OrderCart order);

    List<CartOrderProductList> findBySellerIdAndStatusInAndUpdatedAtAfter(int id, List<String> statuses, LocalDateTime fromDate);
}
