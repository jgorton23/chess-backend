package com.jacob.backend.controller;

import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.HttpClientErrorException.Unauthorized;

import com.jacob.backend.data.Model.*;
import com.jacob.backend.responses.JSONResponses;
import com.jacob.backend.service.GameService;
import com.jacob.backend.service.SessionService;

@RestController
@CrossOrigin(origins = "http://localhost:4200", allowCredentials = "true")
@RequestMapping("/game")
public class GameController {

    @Autowired
    private GameService gameService;

    @Autowired
    private SessionService sessionService;

    @PostMapping("/new")
    public ResponseEntity<String> newGame(
            @CookieValue(name = "session-id", defaultValue = "") String sessionId,
            @RequestBody Game game) {
        try {
            String username = sessionService.getUsernameById(sessionId);

            if (!username.equals(game.getBlackPlayerUsername()) && !username.equals(game.getWhitePlayerUsername())) {
                return ResponseEntity.badRequest().body(JSONResponses.error("Unauthorized").toString());
            }
            String gameId = gameService.create(game);

            return ResponseEntity.ok().body(JSONResponses.objectBuilder().add("gameId", gameId).build().toString());
        } catch (OutOfMemoryError e) {
            return ResponseEntity.status(401).body(JSONResponses.unauthorized().toString());
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(JSONResponses.error(e.getMessage()).toString());
        }
    }
}
