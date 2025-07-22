package com.example.imagedemo.service;

import com.example.imagedemo.model.Cart;
import com.example.imagedemo.model.OrderCart;
import com.example.imagedemo.model.Product;
import com.example.imagedemo.model.CartOrderProductList;
import com.example.imagedemo.repository.cartOrderProductItemRepository;
import org.hibernate.query.Order;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class cartOrderProductService {
    @Autowired
    private cartOrderProductItemRepository cartRepo;

    public void additem(CartOrderProductList itemsCart) {
        cartRepo.save(itemsCart);
    }

    public void deleteItem(CartOrderProductList itemToDelete) {
        cartRepo.delete(itemToDelete);
    }

    public Page<CartOrderProductList> getAllItemsOfUser(Cart cart, Pageable pageable, String status) {
        return cartRepo.findAllByCartAndStatus(cart, pageable, status);
    }

    public void deleteByCart(Cart c) {
        cartRepo.deleteAllByCart(c);
    }

    public CartOrderProductList getSpecificItems(Cart c, Product p, String active) {
        return cartRepo.findByCartAndProductAndStatus(c, p, active);
    }

    public Page<CartOrderProductList> getAllProductsOfOrder(OrderCart order, Pageable pageable) {
        return cartRepo.findByOrder(order, pageable);
    }

    public CartOrderProductList getProductToReturn(Cart c, Product p, OrderCart order) {
        return cartRepo.findByCartAndProductAndOrder(c, p, order);
    }

    public Page<CartOrderProductList> getAllItemsOfOrder(OrderCart order, Pageable pageable) {
        return cartRepo.findByOrder(order, pageable);
    }

    public List<CartOrderProductList> getAllItems(int id, List<String> statuses, LocalDateTime fromDate) {
        return cartRepo.findBySellerIdAndStatusInAndUpdatedAtAfter(id , statuses , fromDate);
    }
}
