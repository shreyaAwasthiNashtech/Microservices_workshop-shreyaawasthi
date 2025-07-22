package com.evolve.userservice.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

@RestController
public class UserController {

    // @GetMapping
    // public String greetUser(@RequestParam(defaultValue = "World") String name) {
    //     return "Hello, " + name;
    // }

    @GetMapping("/users")
    public String getUsers() {
        return "User service is working!";
    }

     @GetMapping("/user/test")
    public String hello() {
        return "Hello from user-service";
    }
}
