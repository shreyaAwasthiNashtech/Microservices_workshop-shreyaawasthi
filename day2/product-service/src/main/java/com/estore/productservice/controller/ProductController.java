package com.estore.productservice.controller;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RefreshScope  
public class ProductController {

    @Value("${product.welcome-message}")
    private String welcomeMessage;

    @GetMapping("/products")
    public String getProducts() {
        return welcomeMessage;
    }
    
    @GetMapping("/test")
    public String test() {
        return "Product service is working!";
    }
}
