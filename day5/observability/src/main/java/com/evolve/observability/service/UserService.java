package com.evolve.observability.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.Random;

@Service
public class UserService {

    private static final Logger log = LoggerFactory.getLogger(UserService.class);

    public String getUserById(String id) {
        try {
            int delay = new Random().nextInt(400) + 100; // 100–500ms delay
            Thread.sleep(delay);
            log.info("Fetched user id={} after delay={}ms", id, delay);
        } catch (InterruptedException e) {
            log.error("Interrupted while simulating delay", e);
            Thread.currentThread().interrupt();
        }
        return "User_" + id;
    }
}
