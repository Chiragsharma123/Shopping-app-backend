package com.example.imagedemo.controller;
import com.example.imagedemo.common.ResponseDto;
import com.example.imagedemo.common.Status;
import com.example.imagedemo.dto.PagingDto;
import com.example.imagedemo.dto.productRequestDto;
import com.example.imagedemo.impl.CartManagerImpl;
import com.example.imagedemo.service.cartOrderProductService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/cart")
public class cartController {
    Logger logger = LoggerFactory.getLogger(cartController.class);
    @Autowired
    private cartOrderProductService cartservice;
    @Autowired
    private CartManagerImpl cartManager;

    @PostMapping("/productAdd")
    public ResponseDto<?> addProductToCart(@RequestBody productRequestDto P, @RequestHeader("Request-id") int requestId) {
        try {
            int quantity=P.getQuantity();
            Authentication auth = SecurityContextHolder.getContext().getAuthentication();
            if (auth == null || !auth.isAuthenticated() || auth.getPrincipal().equals("anonymousUser")) {
                logger.error("No user logged in");
                return new ResponseDto<>(Status.BAD_REQUEST.getStatusCode().value(), Status.BAD_REQUEST.getStatusDescription(), requestId, "you need to logged in for accessing the cart", null);
            }
            String username = ((UserDetails) auth.getPrincipal()).getUsername();
            return cartManager.addProductToCart(P, username, requestId);
        } catch (Exception e) {
            return new ResponseDto<>(Status.INTERNAL_ERROR.getStatusCode().value(), Status.INTERNAL_ERROR.getStatusDescription(), requestId, e.getMessage(), null);
        }
    }

    @DeleteMapping("/productDelete")
    public ResponseDto<?> removeProductFromCart(@RequestBody productRequestDto P, @RequestHeader("Request-id") int requestId) {
        try {
            int p_id= P.getPId();
            return cartManager.removeProductFromCart(p_id, requestId);
        } catch (Exception e) {
            return new ResponseDto<>(Status.INTERNAL_ERROR.getStatusCode().value(), Status.INTERNAL_ERROR.getStatusDescription(), requestId, e.getMessage(), null);
        }
    }

    @GetMapping("/products")
    public ResponseDto<?> getAllProductsFromCart(@RequestHeader("Request-id") int requestId, @RequestBody PagingDto request) {
        try {
            int page = request.getPage();
            int size=request.getSize();
            Pageable pageable= PageRequest.of(page , size);
            return cartManager.getAllProductsFromCart(requestId,pageable);
        } catch (Exception e) {
            return new ResponseDto<>(Status.INTERNAL_ERROR.getStatusCode().value(), Status.INTERNAL_ERROR.getStatusDescription(), requestId, e.getMessage(), null);
        }
    }

    @PatchMapping("/quantity")
    public ResponseDto<?> UpdateQuantity(@RequestBody productRequestDto P, @RequestHeader("Request-id") int requestId) {
        try {
            int pId=P.getPId();
            int quantity = P.getQuantity();
            return cartManager.updateQuantity(pId, quantity, requestId);
        } catch (Exception e) {
            return new ResponseDto<>(Status.INTERNAL_ERROR.getStatusCode().value(), Status.INTERNAL_ERROR.getStatusDescription(), requestId, e.getMessage(), null);
        }
    }
}
