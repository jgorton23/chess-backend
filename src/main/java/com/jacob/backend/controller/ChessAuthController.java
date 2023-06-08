package com.jacob.backend.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import com.jacob.backend.data.CredentialsDTO;
import com.jacob.backend.service.ChessAuthService;

import javax.json.Json;
import javax.json.JsonArrayBuilder;
import javax.json.JsonBuilderFactory;
import javax.json.JsonObject;
import javax.json.JsonObjectBuilder;
import javax.json.JsonWriter;

@RestController
@RequestMapping("/auth")
public class ChessAuthController {

    @Autowired
    private ChessAuthService authService;

    private JsonBuilderFactory builderFactory = Json.createBuilderFactory(null);

    @PostMapping("/login")
    public ResponseEntity<JsonObject> login(@RequestBody CredentialsDTO creds) {
        String message;
        try {
            message = authService.login(creds);
            return ResponseEntity.ok().body(builderFactory.createObjectBuilder().add("msg", message).build());
        } catch (Exception e) {
            message = String.format("Error: $v", e);
            return ResponseEntity.badRequest().body(builderFactory.createObjectBuilder().add("msg", message).build());
        }
    }

    @PostMapping("/register")
    public ResponseEntity<JsonObject> register(@RequestBody CredentialsDTO creds) {
        String message;
        ResponseCookie cookie = ResponseCookie.from("user-id", null).maxAge(0).build();
        try {
            message = authService.register(creds);
            cookie = ResponseCookie.from("session-id", "testCookie").maxAge(7200).build();
            return ResponseEntity.ok().header(HttpHeaders.SET_COOKIE, cookie.toString())
                    .body(builderFactory.createObjectBuilder().add("msg", message).build());
        } catch (Exception e) {
            message = String.format("Error: $v", e);
            return ResponseEntity.badRequest().header(HttpHeaders.SET_COOKIE, cookie.toString())
                    .body(builderFactory.createObjectBuilder().add("msg", message).build());
        }
    }

    @PostMapping("/logout")
    public ResponseEntity<JsonObject> logout(@CookieValue(name = "session-id", defaultValue = "") String sessionId) {
        if (sessionId.length() > 0) {
            // TODO - remove session from session db
        }
        ResponseCookie deleteCookie = ResponseCookie.from("session-id", null).build();
        return ResponseEntity.ok().header(HttpHeaders.SET_COOKIE, deleteCookie.toString())
                .body(builderFactory.createObjectBuilder().add("msg", "success").build());
    }
}
