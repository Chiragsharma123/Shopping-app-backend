package com.example.imagedemo.repository;

import com.example.imagedemo.common.ResponseDto;
import com.example.imagedemo.model.Coupon;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface couponRepo extends JpaRepository<Coupon , Integer> {
    List<Coupon>findByStatus(String active);

    List<Coupon> findByStatusAndCountIsOrAndExpiresAtBefore(String active,int count, LocalDateTime now);
}
