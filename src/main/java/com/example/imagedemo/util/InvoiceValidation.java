package com.example.imagedemo.util;

import com.example.imagedemo.common.ResponseDto;
import com.example.imagedemo.dto.InvoiceRequestDto;
import org.springframework.data.domain.Pageable;

public interface InvoiceValidation {
    ResponseDto<?> getInvoicePdfGenerator(InvoiceRequestDto request, int requestId , String PDF_DIR) throws Exception;
}
