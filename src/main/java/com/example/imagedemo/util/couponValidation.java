package com.example.imagedemo.util;

import com.example.imagedemo.common.ResponseDto;
import com.example.imagedemo.dto.couponRequestDto;
import org.springframework.data.domain.Pageable;

public interface couponValidation {
    ResponseDto<?> createDiscountCoupon(int requestId, couponRequestDto couponRequestDto)throws Exception;

    ResponseDto<?> getAllActiveCoupon(int requestId)throws Exception;

    ResponseDto<?> ApplyCoupon(int requestId,couponRequestDto Coupon)throws Exception;

    ResponseDto<?> removeCoupon(int requestId, Pageable pageable)throws Exception;
}
