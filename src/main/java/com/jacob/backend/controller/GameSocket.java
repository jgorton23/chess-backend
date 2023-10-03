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
import com.jacob.backend.responses.JSONResponses;
import com.jacob.backend.service.GameService;

import lombok.extern.apachecommons.CommonsLog;

/**
 * Controller containing endpoints for the WebSocket
 */
@RestController
@CrossOrigin
@CommonsLog
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
     * @param move   the move that will be done before sending the updated game
     *               state across the ws channel
     */
    @MessageMapping("/game/{gameId}")
    public void updateGame(@DestinationVariable String gameId, MoveDTO move) {

        try {

            log.info(String.format("WebSocket message received | URL: '%s', Data: '%s'",
                    "/game/" + gameId,
                    JSONResponses.toJson(move)));

            // perform the move
            gameService.doMove(move.getPlayerUsername(), gameId, move);

            // get the new state of the game
            Game game = gameService.findById(UUID.fromString(gameId));

            // send the game to the other users
            messaging.convertAndSend("/topic/game/" + gameId, game);

            log.info(String.format("Websocket message sent | URL: '%s', Data: '%s'",
                    "/topic/game/" + gameId,
                    JSONResponses.toJson(game)));

        } catch (Exception e) {

            log.error(e.getMessage());

        }

    }

    /**
     * Broadcasts a chat message to all users subscribed to the game room chat
     * 
     * @param gameId  the UUID of the game in which the chat was sent, and to which
     *                the chat will be broadcast
     * @param message the content of the chat
     */
    @MessageMapping("/game/{gameId}/chat")
    public void chat(@DestinationVariable String gameId, String message) {

        log.info(String.format("WebSocket message received | URL: '%s', Data: '%s'",
                "/game/" + gameId + "/chat",
                JSONResponses.toJson(message)));

        messaging.convertAndSend("/topic/game/" + gameId + "/chat", message);

        log.info(String.format("Websocket message sent | URL: '%s', Data: '%s'",
                "/topic/game/" + gameId + "/chat",
                JSONResponses.toJson(message)));

    }

    /**
     * Updates the game and broadcasts the resignation to the players of the game
     * 
     * @param gameId  the UUID of the game in which someone resigned
     * @param message the username of the user who resigned
     */
    @MessageMapping("/game/{gameId}/resign")
    public void resign(@DestinationVariable String gameId, String username) {

        try {

            log.info(String.format("WebSocket message received | URL: '%s', Data: '%s'",
                    "/game/" + gameId + "/resign",
                    JSONResponses.toJson(username)));

            gameService.resign(username, gameId);

            messaging.convertAndSend("/topic/game/" + gameId + "/resign", username);

            log.info(String.format("Websocket message sent | URL: '%s', Data: '%s'",
                    "/topic/game/" + gameId + "/resign",
                    JSONResponses.toJson(username)));

        } catch (Exception e) {

            log.error(e.getMessage());

        }

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
    public void offerRematch(@DestinationVariable String gameId, RematchDTO rematchRequest) {

        log.info(String.format("WebSocket message received | URL: '%s', Data: '%s'",
                "/game/" + gameId + "/rematch",
                JSONResponses.toJson(rematchRequest)));

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

        log.info(String.format("Websocket message sent | URL: '%s', Data: '%s'",
                "/topic/game/" + gameId + "/resign",
                JSONResponses.toJson(rematchRequest)));

    }

    @MessageMapping("/game/{gameId}/timeout")
    public void timeout(@DestinationVariable String gameId, String username) {

        log.info(String.format("WebSocket message received | URL: '%s', Data: '%s'",
                "/game/" + gameId + "/timeout",
                username));

        gameService.timeout(gameId, username);

        log.info(String.format("WebSocket message sent | URL: '%s', Data: '%s'",
                "/topic/game/" + gameId + "/timeout",
                username));

    }

}
