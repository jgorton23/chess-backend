package com.jacob.backend.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
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
    public ResponseEntity<String> login(@RequestBody CredentialsDTO creds) {
        String message;
        HttpStatus status;
        try {
            message = authService.login(creds);
            status = HttpStatus.OK;
        } catch (Exception e) {
            message = String.format("Error: $v", e);
            status = HttpStatus.BAD_REQUEST;
        }
        ResponseEntity<String> r = new ResponseEntity<String>(message, null, status);
        return r;
    }

    @PostMapping("/register")
    public ResponseEntity<String> register(@RequestBody CredentialsDTO creds) {
        String message;
        HttpStatus status;
        try {
            message = authService.register(creds);
            status = HttpStatus.OK;
        } catch (Exception e) {
            message = String.format("Error: $v", e);
            status = HttpStatus.BAD_REQUEST;
        }
        ResponseEntity<String> r = new ResponseEntity<String>(message, null, status);
        return r;
    }
}
