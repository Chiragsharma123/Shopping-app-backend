package com.example.imagedemo.controller;

import com.example.imagedemo.common.ResponseDto;
import com.example.imagedemo.common.Status;
import com.example.imagedemo.dto.InvoiceRequestDto;
import com.example.imagedemo.impl.InvoiceManagerImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.*;

import java.nio.file.Paths;

@RestController
@RequestMapping("/invoice")
public class InvoiceController {
    @Autowired
    private InvoiceManagerImpl invoiceManager;
    private static final String PDF_DIR="D:\\Ecommerce\\Invoices";
    @GetMapping("/generate")
    public ResponseDto<?>getInvoiceForOrder(@RequestBody InvoiceRequestDto request, @RequestHeader("Request-id") int requestId ){
        try{
            return invoiceManager.getInvoicePdfGenerator(request,requestId ,PDF_DIR);
        }catch (Exception e) {
            return new ResponseDto<>(Status.INTERNAL_ERROR.getStatusCode().value(), Status.INTERNAL_ERROR.getStatusDescription(), requestId, e.getMessage(), null);
        }
    }
}
