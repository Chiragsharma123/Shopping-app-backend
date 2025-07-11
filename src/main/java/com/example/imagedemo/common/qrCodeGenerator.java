package com.example.imagedemo.common;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.EncodeHintType;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.client.j2se.MatrixToImageWriter;
import com.google.zxing.common.BitMatrix;
import org.springframework.stereotype.Component;
import java.io.File;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;

@Component
public class qrCodeGenerator {
    public String generateQRCodeImage(String text,String fileName ,int width, int height) throws Exception {
        Map<EncodeHintType, Object> hints = new HashMap<>();
        hints.put(EncodeHintType.CHARACTER_SET, "UTF-8");
        BitMatrix bitMatrix = new MultiFormatWriter().encode(text, BarcodeFormat.QR_CODE, width, height, hints);
        String relativePath = "D:\\Ecommerce\\imagedemo\\src\\main\\resources\\static"+ fileName;
        Path path= new File(relativePath).toPath();
        File parent = path.toFile().getParentFile();
        if (!parent.exists()) {
            parent.mkdirs();
        }
        MatrixToImageWriter.writeToPath(bitMatrix,"PNG",path);
        return relativePath;
    }
}
