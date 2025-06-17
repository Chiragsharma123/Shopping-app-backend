package com.example.imagedemo.repository;

import com.example.imagedemo.common.ResponseDto;
import com.example.imagedemo.model.Coupon;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface couponRepo extends JpaRepository<Coupon , Integer> {
    List<Coupon>findByStatusAndExpiresAtAfter(String active, LocalDateTime time);

    List<Coupon> findByStatusAndExpiresAtBefore(String active, LocalDateTime now);
}
