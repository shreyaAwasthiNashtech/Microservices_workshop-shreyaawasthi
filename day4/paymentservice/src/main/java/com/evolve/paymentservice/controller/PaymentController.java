package com.evolve.Payment_Application.controller;

import com.evolve.Payment_Application.service.PaymentService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.concurrent.CompletableFuture;

@RestController
public class PaymentController {

    private final PaymentService paymentService;

    public PaymentController(PaymentService paymentService) {
        this.paymentService = paymentService;
    }

    @GetMapping("/payment/retry")
    public CompletableFuture<String> retryPaymentEndpoint() {
        return paymentService.makePayment();
    }
}
