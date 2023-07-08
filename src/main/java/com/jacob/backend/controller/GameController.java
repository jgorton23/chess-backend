package com.jacob.backend.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.jacob.backend.data.Model.*;
import com.jacob.backend.responses.JSONResponses;
import com.jacob.backend.responses.exceptions.UnauthorizedException;
import com.jacob.backend.service.GameService;
import com.jacob.backend.service.SessionService;

/**
 * Controller containing Game related endpoints
 */
@RestController
@CrossOrigin(origins = "http://localhost:4200", allowCredentials = "true")
@RequestMapping("/game")
public class GameController {

    /**
     * Service containing Game related logic
     */
    @Autowired
    private GameService gameService;

    /**
     * Service containing Session related logic
     */
    @Autowired
    private SessionService sessionService;

    /**
     * Created a new Game with the given initial gameState
     * 
     * @param sessionId the id of the Session identifying the User creating the Game
     * @param game      the initial gameState
     * @return id of the Game if the operation was successful, else 4XX
     */
    @PostMapping("/new")
    public ResponseEntity<String> newGame(
            @CookieValue(name = "session-id", defaultValue = "") String sessionId,
            @RequestBody Game game) {
        try {

            // get the Username - throws Unauthorized
            String username = sessionService.getUsernameById(sessionId);

            // make sure the current User is one of the game Players
            if (!username.equals(game.getBlackPlayerUsername()) && !username.equals(game.getWhitePlayerUsername())) {
                throw new UnauthorizedException("current User must be one of the Game players");
            }

            // perform Create
            String gameId = gameService.create(game);

            // return gameId
            return ResponseEntity.ok().body(JSONResponses.objectBuilder().add("gameId", gameId).build().toString());

        } catch (UnauthorizedException e) {

            // catch Unauthorized - return 401
            return ResponseEntity.status(401).body(JSONResponses.unauthorized().toString());

        } catch (Exception e) {

            // catch generic Exception - return badRequest
            return ResponseEntity.badRequest().body(JSONResponses.error(e.getMessage()).toString());

        }
    }
}
