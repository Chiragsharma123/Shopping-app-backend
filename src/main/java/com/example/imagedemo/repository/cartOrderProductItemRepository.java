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

@Repository
public interface cartOrderProductItemRepository extends JpaRepository<CartOrderProductList, Integer> {
    Page<CartOrderProductList> findAllByCartAndStatus(Cart cart , Pageable pageable,String status);
    CartOrderProductList findByCartAndProduct(Cart cart , Product p);
    void deleteAllByCart(Cart cart);

    Page<CartOrderProductList> findByOrder(OrderCart order, Pageable pageable);

    CartOrderProductList findByCartAndProductAndOrder(Cart c, Product p, OrderCart order);
}
