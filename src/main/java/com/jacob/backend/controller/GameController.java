package com.jacob.backend.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.jacob.backend.data.Model.*;
import com.jacob.backend.responses.JSONResponses;
import com.jacob.backend.service.GameService;

@RestController
@RequestMapping("/game")
public class GameController {

    @Autowired
    private GameService gameService;

    @PostMapping("/new")
    public ResponseEntity<String> newGame(
            @CookieValue(name = "session-id", defaultValue = "") String sessionId,
            @RequestBody Game game) {
        try {

            return ResponseEntity.badRequest().body(JSONResponses.error("unimplemented").toString());
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(JSONResponses.error(e.getMessage()).toString());
        }
    }

    @PostMapping("/join")
    public ResponseEntity<String> joinGame() {
        try {
            return ResponseEntity.badRequest().body(JSONResponses.error("unimplemented").toString());
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(JSONResponses.error(e.getMessage()).toString());
        }
    }

    @PostMapping("/leave")
    public ResponseEntity<String> leaveGame() {
        try {
            return ResponseEntity.badRequest().body(JSONResponses.error("unimplemented").toString());
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(JSONResponses.error(e.getMessage()).toString());
        }
    }
}
