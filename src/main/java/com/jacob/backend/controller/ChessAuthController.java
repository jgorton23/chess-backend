package com.jacob.backend.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.GetMapping;

import com.jacob.backend.service.ChessAuthService;

@RestController
@RequestMapping("/auth")
public class ChessAuthController {

    @Autowired
    private ChessAuthService chessAuthService;

    @GetMapping("/login")
    public String login() {
        return "logging in";
    }

    @GetMapping("/register")
    public String register() {
        return "registered";
    }
}
