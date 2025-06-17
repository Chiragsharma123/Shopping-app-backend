package com.example.imagedemo.controller;

import com.example.imagedemo.common.ResponseDto;
import com.example.imagedemo.common.Status;
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
    public ResponseDto<?>createDiscountCoupon(@RequestBody couponRequestDto couponRequestDto, @RequestHeader("Request-id") int requestId){
        try {
            System.out.println(couponRequestDto);
            return couponManager.createDiscountCoupon(requestId,couponRequestDto);
        }catch (Exception e) {
            return new ResponseDto<>(Status.INTERNAL_ERROR.getStatusCode().value(), Status.INTERNAL_ERROR.getStatusDescription(), requestId, e.getMessage(), null);
        }
    }

    @GetMapping("/fetchList")
    public ResponseDto<?>fetchAllTheActiveCoupons(@RequestHeader("Request-id") int requestId){
        try {
            return couponManager.getAllActiveCoupon(requestId);
        }catch (Exception e) {
            return new ResponseDto<>(Status.INTERNAL_ERROR.getStatusCode().value(), Status.INTERNAL_ERROR.getStatusDescription(), requestId, e.getMessage(), null);
        }
    }
    @PostMapping("/update")
    public ResponseDto<?>updateExpiredCoupons(@RequestHeader("Request-id")int requestId){
        try{
            return couponManager.updateCoupons(requestId);
        }catch (Exception e) {
            return new ResponseDto<>(Status.INTERNAL_ERROR.getStatusCode().value(), Status.INTERNAL_ERROR.getStatusDescription(), requestId, e.getMessage(), null);
        }
    }
    @PutMapping("/update/{cId}")
    public ResponseDto<?>updateExistingCoupon(@PathVariable int cId ,@RequestBody couponRequestDto couponRequestDto, @RequestHeader("Request-id") int requestId){
        try{
            return couponManager.updateExistingCoupon(requestId , cId , couponRequestDto);
        }catch (Exception e) {
            return new ResponseDto<>(Status.INTERNAL_ERROR.getStatusCode().value(), Status.INTERNAL_ERROR.getStatusDescription(), requestId, e.getMessage(), null);
        }
    }

    @PostMapping("/Apply/{cId}")
    public ResponseDto<?>ApplyCoupon(@PathVariable int cId ,  @RequestHeader("Request-id") int requestId,@RequestParam int page , @RequestParam int size){
        try{
            Pageable pageable= PageRequest.of(page , size);
            return couponManager.ApplyCoupon(requestId,cId , pageable);
        }catch (Exception e) {
            return new ResponseDto<>(Status.INTERNAL_ERROR.getStatusCode().value(), Status.INTERNAL_ERROR.getStatusDescription(), requestId, e.getMessage(), null);
        }
    }
}
