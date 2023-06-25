package com.jacob.backend.controller;

import java.util.List;
import java.util.UUID;

import javax.json.JsonObject;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.jacob.backend.data.DTO.*;
import com.jacob.backend.data.Model.*;
import com.jacob.backend.service.*;
import com.jacob.backend.responses.JSONResponses;

@RestController
@CrossOrigin(origins = "http://localhost:4200", allowCredentials = "true")
@RequestMapping("/user")
public class UserController {
    @Autowired
    private SessionService sessionService;

    @Autowired
    private UserService userService;

    @Autowired
    private FriendService friendService;

    @Autowired
    private GameService gameService;

    @PostMapping("/friends")
    public ResponseEntity<String> addFriend(
            @CookieValue(name = "session-id", defaultValue = "") String sessionId,
            @RequestBody CredentialsDTO friendUsernameDTO) {
        try {
            Session s = sessionService.findById(UUID.fromString(sessionId));
            String username = s.getUsername();
            friendService.addByUsernames(username, friendUsernameDTO.getUsername());
            return ResponseEntity.ok().body(JSONResponses.success().toString());
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(JSONResponses.error(e.getMessage()).toString());
        }
    }

    @GetMapping("/friends")
    public ResponseEntity<String> getFriends(
            @CookieValue(name = "session-id", defaultValue = "") String sessionId,
            @RequestParam(required = false) Boolean pending) {
        try {
            Session s = sessionService.findById(UUID.fromString(sessionId));
            String username = s.getUsername();

            List<FriendDTO> friends = friendService.findByUsername(username, pending != null && pending);

            JsonObject result = JSONResponses
                    .objectBuilder()
                    .add("friends", JSONResponses.ListToJsonArray(friends))
                    .build();
            return ResponseEntity.ok().body(result.toString());
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(JSONResponses.error(e.getMessage()).toString());
        }
    }

    @PutMapping("/profile")
    public ResponseEntity<String> editProfile(
            @CookieValue(name = "session-id", defaultValue = "") String sessionId,
            @RequestBody CredentialsDTO creds) {
        try {
            Session s = sessionService.findById(UUID.fromString(sessionId));
            String username = s.getUsername();
            String newUsername = creds.getUsername() != null ? creds.getUsername() : username;

            userService.update(username, creds);
            sessionService.update(UUID.fromString(sessionId), newUsername);

            return ResponseEntity.ok().body(JSONResponses.success().toString());
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(JSONResponses.error(e.getMessage()).toString());
        }
    }

    @GetMapping("/profile")
    public ResponseEntity<String> getProfile(
            @CookieValue(name = "session-id", defaultValue = "") String sessionId) {
        try {
            Session s = sessionService.findById(UUID.fromString(sessionId));
            String username = s.getUsername();

            ProfileDTO profile = userService.getProfile(username);

            return ResponseEntity.ok().body(profile.toJson().toString());
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(JSONResponses.error(e.getMessage()).toString());
        }
    }

    @GetMapping("/games")
    public ResponseEntity<String> getGames(@CookieValue(name = "session-id", defaultValue = "") String sessionId) {
        try {
            Session s = sessionService.findById(UUID.fromString(sessionId));
            String username = s.getUsername();
            List<Game> games = gameService.findAllByUsername(username);
            JsonObject result = JSONResponses
                    .objectBuilder()
                    .add("games", JSONResponses.ListToJsonArray(games))
                    .build();
            return ResponseEntity.ok().body(result.toString());
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(JSONResponses.error(e.getMessage()).toString());
        }
    }
}
