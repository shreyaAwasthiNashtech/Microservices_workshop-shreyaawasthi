package com.evolve.Payment_Application.service;

import io.github.resilience4j.bulkhead.annotation.Bulkhead;
import io.github.resilience4j.retry.annotation.Retry;
import io.github.resilience4j.timelimiter.annotation.TimeLimiter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.concurrent.CompletableFuture;

@Autowired
@Service
public class PaymentService {

    private static final Logger logger = LoggerFactory.getLogger(PaymentService.class);
    private int attempt = 1;

    @Retry(name = "paymentRetry", fallbackMethod = "retryFallback")
    @TimeLimiter(name = "paymentRetry", fallbackMethod = "timeoutFallback")
    @Bulkhead(name = "paymentRetry", type = Bulkhead.Type.THREADPOOL, fallbackMethod = "bulkheadFallback")
    public CompletableFuture<String> makePayment() {
        logger.info("Attempt #{} to process payment", attempt++);
        return CompletableFuture.supplyAsync(() -> {
            double rand = Math.random();
            if (rand < 0.5) {
                logger.info("Simulating delay > 2s to trigger timeout...");
                try {
                    Thread.sleep(3000); 
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }
            } else {
                logger.warn("Simulating external failure");
                throw new RuntimeException("Simulated external failure");
            }
            return "Payment processed successfully!";
        });
    }

    public CompletableFuture<String> retryFallback(Throwable ex) {
        logger.error("Retry fallback triggered due to: {}", ex.toString());
        return CompletableFuture.completedFuture("Payment failed after retries.");
    }

    public CompletableFuture<String> timeoutFallback(Throwable ex) {
        logger.error("Timeout fallback triggered due to: {}", ex.toString());
        return CompletableFuture.completedFuture("Payment timed out.");
    }

    public CompletableFuture<String> bulkheadFallback(Throwable ex) {
        logger.error("Bulkhead fallback triggered due to: {}", ex.toString());
        return CompletableFuture.completedFuture("Too many requests. Please try again later.");
    }
}
