package com.jacob.backend.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import com.jacob.backend.service.AuthService;
import com.jacob.backend.service.SessionService;
import com.jacob.backend.data.DTO.CredentialsDTO;
import com.jacob.backend.responses.JSONResponses;

import java.util.UUID;

/**
 * Controller with Auth related endpoints
 */
@RestController
@CrossOrigin(origins = "http://localhost:4200", allowCredentials = "true")
@RequestMapping("/auth")
public class AuthController {

    /**
     * Service with Auth related logic
     */
    @Autowired
    private AuthService authService;

    /**
     * Service with Session related logic
     */
    @Autowired
    private SessionService sessionService;

    /**
     * If the given credentials are valid, store new Session in db
     * 
     * @param creds the Credentials the user supplied to log in
     * @return a cookie with the UUID of the newly created Session
     */
    @PostMapping("/login")
    public ResponseEntity<String> login(@RequestBody CredentialsDTO creds) {

        // Build a default cookie with no content
        ResponseCookie cookie = ResponseCookie
                .from("session-id", null)
                .path("/")
                .maxAge(0)
                .build();

        try {

            // Perform the login
            String sessionId = authService.login(creds);

            // update the Cookie with the newly created SessionId
            cookie = ResponseCookie
                    .from("session-id", sessionId)
                    .path("/")
                    .maxAge(7200).build();

            // return successful with the Cookie
            return ResponseEntity
                    .ok()
                    .header(HttpHeaders.SET_COOKIE, cookie.toString())
                    .body(JSONResponses.success().toString());

        } catch (Exception e) {

            // catch generic Exceptions - return badRequest with null Cookie
            return ResponseEntity
                    .badRequest()
                    .header(HttpHeaders.SET_COOKIE, cookie.toString())
                    .body(JSONResponses.error(e.getMessage()).toString());

        }
    }

    /**
     * Registers a new User with the given Credentials
     * 
     * @param creds the Credentials of the new User
     * @return success message if the operation is successful, else 4XX
     */
    @PostMapping("/register")
    public ResponseEntity<String> register(@RequestBody CredentialsDTO creds) {
        try {

            // perform the Register
            authService.register(creds);

            // return successful
            return ResponseEntity.ok().body(JSONResponses.success().toString());

        } catch (Exception e) {

            // catch generic Exception - return badRequest
            return ResponseEntity.badRequest().body(JSONResponses.error(e.getMessage()).toString());

        }
    }

    /**
     * Removes the Session from the db and empties the Cookie
     * 
     * @param sessionId The UUID of the Session to remove
     * @return successful
     */
    @DeleteMapping("/logout")
    public ResponseEntity<String> logout(@CookieValue(name = "session-id", defaultValue = "") String sessionId) {
        try {

            // If the SessionId is not empty, remove it from the db
            if (sessionId.length() > 0) {
                sessionService.deleteById(UUID.fromString(sessionId));
            }

            // Empty the cookie
            ResponseCookie deleteCookie = ResponseCookie.from("session-id", null).path("/").build();

            // Return successful with empty Cookie
            return ResponseEntity
                    .ok()
                    .header(HttpHeaders.SET_COOKIE, deleteCookie.toString())
                    .body(JSONResponses.success().toString());

        } catch (Exception e) {

            // Catch generic Exception - return badRequest
            return ResponseEntity.badRequest().body(JSONResponses.error(e.getMessage()).toString());

        }
    }
}
