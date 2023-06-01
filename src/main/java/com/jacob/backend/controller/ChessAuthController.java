package com.jacob.backend.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import com.jacob.backend.data.Credentials;
import com.jacob.backend.service.ChessAuthService;

@RestController
@RequestMapping("/auth")
public class ChessAuthController {

    @Autowired
    private ChessAuthService authService;

    @PostMapping("/login")
    public String login(@RequestBody Credentials creds) {
        return authService.login(creds);
    }

    @GetMapping("/register")
    public String register() {
        return authService.register();
    }
}
