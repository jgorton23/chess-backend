package com.jacob.backend.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RestController;

import com.jacob.backend.data.DTO.MoveDTO;

/**
 * Controller containing endpoints for the WebSocket
 */
@RestController
@CrossOrigin
public class GameSocket {

    /**
     * Service containing Message related logic
     */
    @Autowired
    private SimpMessagingTemplate messaging;

    /**
     * Updates the Game in the database, and sends the updated gameState to the
     * topoic
     * 
     * @param gameId the Id of the Game to update
     * @param game   the new gameState to save and send
     */
    @MessageMapping("/game/{gameId}")
    public void UpdateGame(@DestinationVariable String gameId, MoveDTO move) {

        // send the game to the other users
        messaging.convertAndSend("/topic/game/" + gameId, move);

    }

    @MessageMapping("/game/{gameId}/chat")
    public void sendChat(@DestinationVariable String gameId, String message) {

        messaging.convertAndSend("/topic/game/" + gameId + "/chat", message);

    }

    @MessageMapping("/game/{gameId}/resign")
    public void sendResign(@DestinationVariable String gameId, String message) {

        messaging.convertAndSend("/topic/game/" + gameId + "/resign", message);

    }
}
