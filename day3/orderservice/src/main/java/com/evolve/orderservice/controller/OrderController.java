package com.evolve.orderservice.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;


@RestController
public class OrderController {

    // @GetMapping
    // public String getOrders(@RequestParam(defaultValue = "World") String name) {
    //     return "Hello, " + name;
    // }

    @GetMapping("/orders")
    public String getOrders() {
        return "Order list from order-service";
    }

    @GetMapping("/order/test")
    public String hello() {
        return "Hello from order-service";
    }
}
