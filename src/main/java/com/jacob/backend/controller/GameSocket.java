package com.jacob.backend.controller;

import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RestController;

import com.jacob.backend.data.DTO.MoveDTO;
import com.jacob.backend.data.DTO.RematchDTO;
import com.jacob.backend.data.Model.Game;
import com.jacob.backend.service.GameService;

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

    @Autowired
    private GameService gameService;

    /**
     * Updates the Game in the database, and sends the updated gameState to the
     * topic
     * 
     * @param gameId the Id of the Game to update
     * @param game   the new gameState to save and send
     */
    @MessageMapping("/game/{gameId}")
    public void UpdateGame(@DestinationVariable String gameId, MoveDTO move) {

        // send the game to the other users
        messaging.convertAndSend("/topic/game/" + gameId, move);

    }

    /**
     * Broadcasts a chat message to all users subscribed to the game room chat
     * 
     * @param gameId  the UUID of the game in which the chat was sent, and to which
     *                the chat will be broadcast
     * @param message the content of the chat
     */
    @MessageMapping("/game/{gameId}/chat")
    public void sendChat(@DestinationVariable String gameId, String message) {

        messaging.convertAndSend("/topic/game/" + gameId + "/chat", message);

    }

    /**
     * Updates the game and broadcasts the resignation to the players of the game
     * 
     * @param gameId  the UUID of the game in which someone resigned
     * @param message the username of the user who resigned
     */
    @MessageMapping("/game/{gameId}/resign")
    public void sendResign(@DestinationVariable String gameId, String message) {

        messaging.convertAndSend("/topic/game/" + gameId + "/resign", message);

    }

    /**
     * Offers or accepts a rematch offer after the game with UUID {@code gameId}
     * ends
     * 
     * @param gameId    the UUID of the game that just ended
     * @param confirmed the players which have sent the rematch offer, if it is both
     *                  then the new game will be created and the gameId will be
     *                  sent
     */
    @MessageMapping("/game/{gameId}/rematch")
    public void sendRematch(@DestinationVariable String gameId, RematchDTO rematchRequest) {

        if (rematchRequest.getBlackPlayerConfirmed() && rematchRequest.getWhitePlayerConfirmed()) {

            Game previousGame = gameService.findById(UUID.fromString(gameId));

            Game newGame = new Game();
            newGame.setBlackPlayerUsername(previousGame.getWhitePlayerUsername());
            newGame.setWhitePlayerUsername(previousGame.getBlackPlayerUsername());
            newGame.setTimeControl(previousGame.getTimeControl());
            newGame.setFEN("rnbqkbnr/pppppppp/8/8/8/8/PPPPPPPP/RNBQKBNR");

            String newGameId = gameService.create(previousGame.getWhitePlayerUsername(), newGame);

            rematchRequest.setNewGameId(newGameId);

        }

        messaging.convertAndSend("/topic/game/" + gameId + "/rematch", rematchRequest);

    }

}
