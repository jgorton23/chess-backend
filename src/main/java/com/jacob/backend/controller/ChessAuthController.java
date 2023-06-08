package com.jacob.backend.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RequestMapping;
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
        HttpStatus status;
        try {
            message = authService.login(creds);
            status = HttpStatus.OK;
        } catch (Exception e) {
            message = String.format("Error: $v", e);
            status = HttpStatus.BAD_REQUEST;
        }
        ResponseEntity<JsonObject> r = new ResponseEntity<JsonObject>(null, null, status);
        return r;
    }

    @PostMapping("/register")
    public ResponseEntity<String> register(@RequestBody CredentialsDTO creds) {
        String message;
        ResponseCookie cookie = ResponseCookie.from("user-id", null).maxAge(0).build();
        try {
            message = authService.register(creds);
            cookie = ResponseCookie.from("user-id", "testCookie").maxAge(7200).build();
            return ResponseEntity.ok().header(HttpHeaders.SET_COOKIE, cookie.toString()).body(message);
        } catch (Exception e) {
            message = String.format("Error: $v", e);
            return ResponseEntity.badRequest().header(HttpHeaders.SET_COOKIE, cookie.toString()).body(message);
        }
    }
}
