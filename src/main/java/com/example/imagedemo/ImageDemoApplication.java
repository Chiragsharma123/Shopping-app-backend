package com.example.imagedemo;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.EnableAspectJAutoProxy;

@SpringBootApplication
@EnableAspectJAutoProxy
public class ImageDemoApplication {

    public static void main(String[] args) {
        SpringApplication.run(ImageDemoApplication.class, args);
    }

}
