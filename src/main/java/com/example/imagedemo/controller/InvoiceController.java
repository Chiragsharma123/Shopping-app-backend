package com.example.imagedemo.controller;

import com.example.imagedemo.common.ResponseDto;
import com.example.imagedemo.common.Status;
import com.example.imagedemo.dto.InvoiceRequestDto;
import com.example.imagedemo.impl.InvoiceManagerImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
@CrossOrigin(origins = "http://localhost:4200")
@RestController
@RequestMapping("/invoice")
public class InvoiceController {
    @Autowired
    private InvoiceManagerImpl invoiceManager;
    private static final String PDF_DIR="D:\\Ecommerce\\Invoices";
    @PostMapping("/generate")
    public ResponseDto<?>getInvoiceForOrder(@RequestBody InvoiceRequestDto request, @RequestHeader("Request-id") int requestId ){
        try{
            return invoiceManager.getInvoicePdfGenerator(request,requestId ,PDF_DIR);
        }catch (Exception e) {
            return new ResponseDto<>(Status.INTERNAL_ERROR.getStatusCode().value(), Status.INTERNAL_ERROR.getStatusDescription(), requestId, e.getMessage(), null);
        }
    }
    @GetMapping("/invoices/{orderId}")
    public ResponseEntity<Resource> getInvoice(@PathVariable Long orderId) throws IOException {
        Path path = Paths.get("D:/Ecommerce/Invoices/invoice_order_" + orderId + ".pdf");
        Resource resource = new UrlResource(path.toUri());

        if (!resource.exists()) {
            return ResponseEntity.notFound().build();
        }

        return ResponseEntity.ok()
                .contentType(MediaType.APPLICATION_PDF)
                .header(HttpHeaders.CONTENT_DISPOSITION, "inline; filename=\"" + resource.getFilename() + "\"")
                .body(resource);
    }

}
