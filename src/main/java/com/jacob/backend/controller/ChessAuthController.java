package com.jacob.backend.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import com.jacob.backend.data.CredentialsDTO;
import com.jacob.backend.service.ChessAuthService;

@RestController
@RequestMapping("/auth")
public class ChessAuthController {

    @Autowired
    private ChessAuthService authService;

    @PostMapping("/login")
    public String login(@RequestBody CredentialsDTO creds) {
        return authService.login(creds);
    }

    @PostMapping("/register")
    public String register(@RequestBody CredentialsDTO creds) {
        return authService.register(creds);
    }
}
