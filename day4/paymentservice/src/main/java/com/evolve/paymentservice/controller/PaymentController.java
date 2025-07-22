package com.evolve.paymentservice.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.concurrent.ThreadLocalRandom;

@RestController
public class PaymentController {

    @GetMapping("/payment/process")
    public String processPayment() throws InterruptedException {
        int random = ThreadLocalRandom.current().nextInt(1, 4);
        if (random == 1) {
            throw new RuntimeException("Payment service failed!"); // simulating failure
        } else {
            Thread.sleep(3000); // simulating the delay
        }
        return "Payment processed successfully!";
    }
}
