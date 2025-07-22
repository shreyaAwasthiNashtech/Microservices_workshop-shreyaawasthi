package com.evolve.orderservice.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class OrderController {

    @GetMapping("/orders")
    public String getOrders() {
        return "Order list from order-service";
    }

    @GetMapping("/hello")
    public String hello() {
        return "Hello from order-service";
    }
}
