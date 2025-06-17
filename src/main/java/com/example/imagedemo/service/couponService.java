package com.example.imagedemo.service;

import com.example.imagedemo.common.ResponseDto;
import com.example.imagedemo.model.Coupon;
import com.example.imagedemo.repository.couponRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class couponService {
    @Autowired
    private couponRepo couponRepo;
    public void saveCoupon(Coupon coupon) {
        couponRepo.save(coupon);
    }

    public List<Coupon> getAllActiveCoupons(String active, LocalDateTime time) {
        return couponRepo.findByStatusAndExpiresAtAfter(active , time);
    }

    public List<Coupon> getAllCoupons() {
        return couponRepo.findAll();
    }

    public List<Coupon> getAllExpiryCoupons(String active, LocalDateTime now) {
        return couponRepo.findByStatusAndExpiresAtBefore(active,now);
    }

    public Coupon findSpecificCoupon(int cId) {
        return couponRepo.findById(cId).orElse(null);
    }
}
