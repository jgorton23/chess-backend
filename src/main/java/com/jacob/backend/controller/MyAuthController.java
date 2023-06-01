package com.jacob.backend.controller;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class MyAuthController {
    @GetMapping("/auth/login")
    public String login() {
        return "logging in";
    }

    @GetMapping("/auth/register")
    public String register() {
        return "registered";
    }
}
