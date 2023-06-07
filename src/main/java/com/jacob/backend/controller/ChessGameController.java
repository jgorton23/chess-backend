package com.jacob.backend.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.jacob.backend.data.Game;
import com.jacob.backend.service.ChessGameService;

@RestController
@RequestMapping("/game")
public class ChessGameController {
    @Autowired
    private ChessGameService gameService;

    @PostMapping
    public ResponseEntity<String> createGame(Game game) {
        try {
            gameService.createGame(game);
            return ResponseEntity.ok().body(null);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
}
