package com.example.imagedemo.util;

import com.example.imagedemo.common.ResponseDto;
import org.springframework.data.domain.Pageable;

public interface InvoiceValidation {
    ResponseDto<?> getInvoicePdfGenerator(int orderId, int requestId, String filePath, Pageable pageable) throws Exception;
}
