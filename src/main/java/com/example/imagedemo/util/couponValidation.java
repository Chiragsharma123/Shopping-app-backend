package com.example.imagedemo.util;

import com.example.imagedemo.common.ResponseDto;
import com.example.imagedemo.dto.couponRequestDto;
import org.springframework.data.domain.Pageable;

public interface couponValidation {
    ResponseDto<?> createDiscountCoupon(int requestId, couponRequestDto couponRequestDto)throws Exception;

    ResponseDto<?> getAllActiveCoupon(int requestId)throws Exception;

    ResponseDto<?> updateCoupons(int requestId)throws Exception;

    ResponseDto<?> updateExistingCoupon(int requestId, int cId, couponRequestDto couponRequestDto)throws Exception;

    ResponseDto<?> ApplyCoupon(int requestId, int cId , Pageable pageable)throws Exception;
}
