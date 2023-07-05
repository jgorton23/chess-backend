package com.jacob.backend.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RestController;

import com.jacob.backend.data.Model.Game;
import com.jacob.backend.service.GameService;

@RestController
@CrossOrigin
public class GameSocket {

    @Autowired
    private GameService gameService;

    @Autowired
    private SimpMessagingTemplate messaging;

    @MessageMapping("/game/{gameId}")
    public void UpdateGame(@DestinationVariable String gameId, Game game) {
        messaging.convertAndSend("/topic/game/" + gameId, game);
        gameService.update(game);
    }
}
