package com.estore.orderservice.controller;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RefreshScope  
public class OrderController {

    @Value("${order.welcome-message}")
    private String welcomeMessage;

    @GetMapping("/orders")
    public String getOrders() {
        return welcomeMessage;
    }

    @GetMapping("/test")
    public String test() {
        return "Order service is working!";
    }
}
