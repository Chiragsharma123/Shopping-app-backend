package com.example.imagedemo.controller;

import com.example.imagedemo.common.ResponseDto;
import com.example.imagedemo.common.Status;
import com.example.imagedemo.dto.PagingDto;
import com.example.imagedemo.dto.couponRequestDto;
import com.example.imagedemo.impl.couponManagerImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/coupon")
public class couponController {
    @Autowired
    private couponManagerImpl couponManager;

    @PostMapping("/create")
    public ResponseDto<?> createDiscountCoupon(@RequestBody couponRequestDto couponRequestDto, @RequestHeader("Request-id") int requestId) {
        try {
            return couponManager.createDiscountCoupon(requestId, couponRequestDto);
        } catch (Exception e) {
            return new ResponseDto<>(Status.INTERNAL_ERROR.getStatusCode().value(), Status.INTERNAL_ERROR.getStatusDescription(), requestId, e.getMessage(), null);
        }
    }

    @GetMapping("/fetchList")
    public ResponseDto<?> fetchAllTheActiveCoupons(@RequestHeader("Request-id") int requestId) {
        try {
            return couponManager.getAllActiveCoupon(requestId);
        } catch (Exception e) {
            return new ResponseDto<>(Status.INTERNAL_ERROR.getStatusCode().value(), Status.INTERNAL_ERROR.getStatusDescription(), requestId, e.getMessage(), null);
        }
    }

    @PostMapping("/ApplyCoupon")
    public ResponseDto<?> ApplyCoupon(@RequestBody couponRequestDto Coupon, @RequestHeader("Request-id") int requestId) {
        try {
            return couponManager.ApplyCoupon(requestId, Coupon);
        } catch (Exception e) {
            return new ResponseDto<>(Status.INTERNAL_ERROR.getStatusCode().value(), Status.INTERNAL_ERROR.getStatusDescription(), requestId, e.getMessage(), null);
        }
    }

    @PostMapping("/remove")
    public ResponseDto<?> removeCoupon(@RequestHeader("Request-id") int requestId, @RequestBody PagingDto Paging) {
        try {
            int page = Paging.getPage();
            int size = Paging.getSize();
            Pageable pageable = PageRequest.of(page, size);
            return couponManager.removeCoupon(requestId, pageable);
        } catch (Exception e) {
            return new ResponseDto<>(Status.INTERNAL_ERROR.getStatusCode().value(), Status.INTERNAL_ERROR.getStatusDescription(), requestId, e.getMessage(), null);
        }
    }

}
