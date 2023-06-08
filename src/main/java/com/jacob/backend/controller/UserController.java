package com.jacob.backend.controller;

import java.util.UUID;

import javax.json.JsonObject;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.jacob.backend.data.DTO.CredentialsDTO;
import com.jacob.backend.data.DTO.ProfileDTO;
import com.jacob.backend.data.Model.Session;
import com.jacob.backend.data.Model.User;
import com.jacob.backend.responses.JSONResponses;
import com.jacob.backend.service.SessionService;
import com.jacob.backend.service.UserService;

@RestController
@RequestMapping("/user")
public class UserController {
    @Autowired
    private SessionService sessionService;

    @Autowired
    private UserService userService;

    @PostMapping("/friends")
    public ResponseEntity<JsonObject> addFriend(
            @CookieValue(name = "session-id", defaultValue = "") String sessionId,
            @RequestBody UUID friendUUID) {
        return null;
    }

    @GetMapping("/friends")
    public ResponseEntity<JsonObject> getFriends(
            @CookieValue(name = "session-id", defaultValue = "") String sessionId) {
        return null;
    }

    @PutMapping("/profile")
    public ResponseEntity<JsonObject> editProfile(
            @CookieValue(name = "session-id", defaultValue = "") String sessionId,
            @RequestBody CredentialsDTO creds) {
        return null;
    }

    @GetMapping("/profile")
    public ResponseEntity<JsonObject> getProfile(
            @CookieValue(name = "session-id", defaultValue = "") String sessionId) {
        try {
            Session s = sessionService.findById(UUID.fromString(sessionId));
            String username = s.getUsername();
            User u = userService.findByUsername(username);
            ProfileDTO profile = new ProfileDTO(10, username, u.getEmail());
            return ResponseEntity.ok().body(profile.toJson());
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(JSONResponses.error(e.getMessage()));
        }
    }

    @GetMapping("/games")
    public ResponseEntity<JsonObject> getGames(@CookieValue(name = "session-id", defaultValue = "") String sessionId) {
        return null;
    }
}
