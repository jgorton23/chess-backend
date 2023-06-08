package com.jacob.backend.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import com.jacob.backend.data.CredentialsDTO;
import com.jacob.backend.service.ChessAuthService;
import com.jacob.backend.service.SessionService;
import com.jacob.backend.responses.JSONResponses;

import java.util.UUID;

import javax.json.JsonObject;

@RestController
@RequestMapping("/auth")
public class ChessAuthController {

    @Autowired
    private ChessAuthService authService;

    @Autowired
    private SessionService sessionService;

    @PostMapping("/login")
    public ResponseEntity<JsonObject> login(@RequestBody CredentialsDTO creds) {
        String sessionId;
        ResponseCookie cookie = ResponseCookie
                .from("session-id", null)
                .path("/")
                .maxAge(0)
                .build();

        try {
            sessionId = authService.login(creds);
            cookie = ResponseCookie
                    .from("session-id", sessionId)
                    .path("/")
                    .maxAge(7200).build();
            return ResponseEntity
                    .ok()
                    .header(HttpHeaders.SET_COOKIE, cookie.toString())
                    .body(JSONResponses.success());
        } catch (Exception e) {
            return ResponseEntity
                    .badRequest()
                    .header(HttpHeaders.SET_COOKIE, cookie.toString())
                    .body(JSONResponses.error(e.getMessage()));
        }
    }

    @PostMapping("/register")
    public ResponseEntity<JsonObject> register(@RequestBody CredentialsDTO creds) {
        try {
            authService.register(creds);
            return ResponseEntity
                    .ok()
                    .body(JSONResponses.success());
        } catch (Exception e) {
            return ResponseEntity
                    .badRequest()
                    .body(JSONResponses.error(e.getMessage()));
        }
    }

    @PostMapping("/logout")
    public ResponseEntity<JsonObject> logout(@CookieValue(name = "session-id", defaultValue = "") String sessionId) {
        if (sessionId.length() > 0) {
            sessionService.delete(UUID.fromString(sessionId));
        }
        ResponseCookie deleteCookie = ResponseCookie.from("session-id", null).path("/").build();
        return ResponseEntity
                .ok()
                .header(HttpHeaders.SET_COOKIE, deleteCookie.toString())
                .body(JSONResponses.success());
    }
}
