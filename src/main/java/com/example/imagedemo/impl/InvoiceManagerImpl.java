package com.example.imagedemo.impl;

import com.example.imagedemo.common.ResponseDto;
import com.example.imagedemo.common.Status;
import com.example.imagedemo.common.qrCodeGenerator;
import com.example.imagedemo.dto.InvoiceRequestDto;
import com.example.imagedemo.model.*;
import com.example.imagedemo.service.cartOrderProductService;
import com.example.imagedemo.service.orderCartService;
import com.example.imagedemo.util.InvoiceValidation;
import com.itextpdf.io.font.constants.StandardFonts;
import com.itextpdf.io.image.ImageData;
import com.itextpdf.io.image.ImageDataFactory;
import com.itextpdf.kernel.font.PdfFontFactory;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.borders.Border;
import com.itextpdf.layout.element.Cell;
import com.itextpdf.layout.element.Image;
import com.itextpdf.layout.element.Paragraph;
import com.itextpdf.layout.element.Table;
import com.itextpdf.layout.properties.HorizontalAlignment;
import com.itextpdf.layout.properties.TextAlignment;
import com.itextpdf.layout.properties.UnitValue;
import com.itextpdf.layout.properties.VerticalAlignment;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Component;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.FileOutputStream;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

@Component
public class InvoiceManagerImpl implements InvoiceValidation {
    Logger logger = LoggerFactory.getLogger(InvoiceManagerImpl.class);
    @Autowired
    private orderCartService orderService;
    @Autowired
    private cartOrderProductService cartService;
    @Autowired
    private qrCodeGenerator qrCodeGenerator;

    @Override
    public ResponseDto<?> getInvoicePdfGenerator(InvoiceRequestDto request, int requestId , String PDF_DIR) throws Exception {
        int page = request.getPaging().getPage();
        int size = request.getPaging().getSize();
        int orderId=request.getOrderId();
        Pageable pageable = PageRequest.of(page , size);
        String filename = "invoice_order_" + orderId + ".pdf";
        String filePath = Paths.get(PDF_DIR, filename).toString();
        logger.info("Generating invoice for the order {}", orderId);
        OrderCart order = orderService.getOrderById(orderId);
        Cart cart = order.getCart();
        users user = cart.getUser();
        Coupon coupon = order.getCoupon();
        Page<CartOrderProductList> itemsCart = cartService.getAllItemsOfOrder(order, pageable);
        if (itemsCart.isEmpty() || order.getStatus().equals("Invoiced") || order.getStatus().equals("Inactive")) {
            logger.error("There is no product in the order {} so invoice can't be generated", orderId);
            return new ResponseDto<>(Status.NOT_FOUND.getStatusCode().value(), Status.NOT_FOUND.getStatusDescription(), requestId, "No itmes is placed in the order", null);
        }
        List<String>ProductsName = new ArrayList<>();
        double discount = 0;
        if (coupon != null) {
            discount = order.getDiscountGivenInRs();
        }
        int count = 1;
        double subtotal = 0;
        for(CartOrderProductList items:itemsCart){
            Product p = items.getProduct();
            ProductsName.add(p.getName());
            double itemTotal = items.getQuantity() * p.getPrice();
            subtotal += itemTotal;
        }
        subtotal -= discount;
        double gst = subtotal * 0.18;
        double total = subtotal + gst;
        PdfWriter writer = new PdfWriter(new FileOutputStream(filePath));
        PdfDocument pdf = new PdfDocument(writer);
        Document document = new Document(pdf);

        String logoPath = "D:\\Ecommerce\\imagedemo\\src\\main\\resources\\static\\myshop_logo_resized.png";
        ImageData logoData = ImageDataFactory.create(logoPath);
        Image logo = new Image(logoData);
        logo.scaleToFit(50, 33);
        logo.setAutoScale(false);

        Table headerTable = new Table(UnitValue.createPercentArray(new float[]{60,30,10})).useAllAvailableWidth().setMarginBottom(20);

        Cell titleCell = new Cell().add(new Paragraph("Ecommerce_INVOICE").setFont(PdfFontFactory.createFont(StandardFonts.HELVETICA_BOLD)).setFontSize(18).setTextAlignment(TextAlignment.LEFT)).setBorder(Border.NO_BORDER).setVerticalAlignment(VerticalAlignment.MIDDLE);

        Cell logoCell = new Cell().add(logo).setTextAlignment(TextAlignment.RIGHT).setPadding(0).setMarginRight(0).setBorder(Border.NO_BORDER).setVerticalAlignment(VerticalAlignment.MIDDLE);

        String qrText = String.format(
                "Customer: %s\nProducts: %s\nTotal Amount: Rs %.2f",
                user.getUsername(),
                ProductsName,
                total
        );
        String fileName= request.getOrderId() + "qr";
        String QrPath = qrCodeGenerator.generateQRCodeImage(qrText, fileName,50, 33);
        ImageData QrData = ImageDataFactory.create(QrPath);
        Image qr = new Image(QrData);
        logo.scaleToFit(50, 33);
        logo.setAutoScale(false);
        Cell qrCell = new Cell();
        qrCell.setBorder(Border.NO_BORDER);
        qrCell.add(qr);
        headerTable.addCell(titleCell);
        headerTable.addCell(logoCell);
        headerTable.addCell(qrCell);

        document.add(headerTable);
        LocalDate today = LocalDate.now();

        document.add(new Paragraph("Customer name: " + user.getUsername()));
        document.add(new Paragraph("Phone: " + user.getPhoneNumber()));
        document.add(new Paragraph("Address: " + user.getAddress()));
        document.add(new Paragraph("Order Date: " + today.format(DateTimeFormatter.ofPattern("yyyy-MM-dd"))));
        document.add(new Paragraph("\n"));

        Table table = new Table(UnitValue.createPercentArray(new float[]{1, 3, 2, 1, 2, 2})).useAllAvailableWidth();
        table.addHeaderCell("S.no");
        table.addHeaderCell("Product Name");
        table.addHeaderCell("Brand");
        table.addHeaderCell("Qty");
        table.addHeaderCell("Unit Price");
        table.addHeaderCell("Subtotal");
        
        for (CartOrderProductList items : itemsCart) {
            if (items.getOrder().getOrderId() == orderId) {
                Product p = items.getProduct();
                double itemTotal = items.getQuantity() * p.getPrice();
                table.addCell(String.valueOf(count++));
                table.addCell(p.getName());
                table.addCell(p.getBrand());
                table.addCell(String.valueOf(items.getQuantity()));
                table.addCell("Rs " + p.getPrice());
                table.addCell("Rs " + itemTotal);
                items.setStatus("Invoiced");
                cartService.additem(items);
            }
        }
        document.add(table);
        document.add(new Paragraph("\n"));
        document.add(new Paragraph("Subtotal: Rs " + String.format("%.2f", subtotal)));
        document.add(new Paragraph("GST (18%): Rs " + String.format("%.2f", gst)));
        if (discount != 0) {
            document.add(new Paragraph("Discount : Rs " + String.format("%.2f", discount)));
        }
       document.add(new Paragraph("Grand Total: Rs " + String.format("%.2f", total)).setBold().setFontSize(14));
        document.add(new Paragraph("\n"));

        String SignaturePath = "D:\\Ecommerce\\imagedemo\\src\\main\\resources\\static\\myshop_signature_resized.png";
        ImageData SignatureData = ImageDataFactory.create(SignaturePath);
        Image sign = new Image(SignatureData);
        sign.scaleToFit(70, 47);
        sign.setAutoScale(false);
        document.add(new Paragraph("Thank you for shopping with MyEcommerceSite.com").setFontColor(com.itextpdf.kernel.colors.ColorConstants.GRAY).setTextAlignment(TextAlignment.LEFT).setMarginTop(20));
        document.add(sign);

        document.close();
        order.setStatus("Invoiced");
        orderService.saveOrder(order);
        return new ResponseDto<>(Status.SUCCESS.getStatusCode().value(), Status.SUCCESS.getStatusDescription(), requestId, "Invoice for the order is generated successfully", filePath);
    }
}
