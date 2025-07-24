package com.evolve.observability.controller;

import com.evolve.observability.service.UserService;
import io.micrometer.core.annotation.Timed;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/user")
public class UserController {

    @Autowired
    private UserService userService;

    @GetMapping("/{id}")
    @Timed(value = "user.api.latency", description = "Time taken to return user info")
    public String getUserById(@PathVariable("id") String id) {
        return userService.getUserById(id);
    }
}
