package com.jacob.backend.controller;

import java.util.List;
import java.util.UUID;

import javax.json.JsonArrayBuilder;
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
import com.jacob.backend.responses.JSONResponses;
import com.jacob.backend.service.FriendService;
import com.jacob.backend.service.SessionService;
import com.jacob.backend.service.UserService;

@RestController
@RequestMapping("/user")
public class UserController {
    @Autowired
    private SessionService sessionService;

    @Autowired
    private UserService userService;

    @Autowired
    private FriendService friendService;

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
            @CookieValue(name = "session-id", defaultValue = "") String sessionId) {
        try {
            Session s = sessionService.findById(UUID.fromString(sessionId));
            String username = s.getUsername();

            List<ProfileDTO> friends = friendService.findByUsername(username);

            JsonArrayBuilder profiles = JSONResponses.arrayBuilder();
            for (ProfileDTO profile : friends) {
                profiles.add(profile.toJson());
            }

            JsonObject result = JSONResponses.objectBuilder().add("friends", profiles).build();
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
        return null;
    }
}
