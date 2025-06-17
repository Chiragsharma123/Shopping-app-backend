package com.example.imagedemo.controller;

import com.example.imagedemo.common.ResponseDto;
import com.example.imagedemo.common.Status;
import com.example.imagedemo.impl.InvoiceManagerImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.*;

import java.nio.file.Paths;

@RestController
@RequestMapping("/invoice")
public class InvoiceController {
    @Autowired
    private InvoiceManagerImpl invoiceManager;
    private static final String PDF_DIR="D:\\Ecommerce\\Invoices";
    @GetMapping("/generate/{orderId}")
    public ResponseDto<?>getInvoiceForOrder(@PathVariable int orderId , @RequestHeader("Request-id") int requestId , @RequestParam int page , @RequestParam int size){
        try{
            Pageable pageable = PageRequest.of(page , size);
            String filename = "invoice_order_" + orderId + ".pdf";
            String filePath = Paths.get(PDF_DIR, filename).toString();
            return invoiceManager.getInvoicePdfGenerator(orderId,requestId,filePath,pageable);
        }catch (Exception e) {
            return new ResponseDto<>(Status.INTERNAL_ERROR.getStatusCode().value(), Status.INTERNAL_ERROR.getStatusDescription(), requestId, e.getMessage(), null);
        }
    }
}
