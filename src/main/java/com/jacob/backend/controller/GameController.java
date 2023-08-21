package com.jacob.backend.controller;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.jacob.backend.data.DTO.MoveDTO;
import com.jacob.backend.data.Model.*;
import com.jacob.backend.responses.JSONResponses;
import com.jacob.backend.responses.exceptions.NotFoundException;
import com.jacob.backend.responses.exceptions.UnauthorizedException;
import com.jacob.backend.service.GameService;
import com.jacob.backend.service.SessionService;

/**
 * Controller containing endpoints related Games
 */
@RestController
@CrossOrigin(origins = "http://localhost:4200", allowCredentials = "true")
@RequestMapping("/games")
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
            String gameId = gameService.create(username, game);

            // return gameId
            return ResponseEntity.ok().body(JSONResponses.toJson("gameId", gameId));

        } catch (UnauthorizedException e) {

            // catch Unauthorized - return 401
            return ResponseEntity.status(401).body(JSONResponses.unauthorized());

        } catch (Exception e) {

            // catch generic Exception - return badRequest
            return ResponseEntity.badRequest().body(JSONResponses.error(e.getMessage()));

        }
    }

    /**
     * Get all Games that the current User has played
     * 
     * @param sessionId the sessionId to identify the current User
     * @return JSON String with the list of Games if the operation was successful,
     *         else 4XX
     */
    @GetMapping
    public ResponseEntity<String> getGames(@CookieValue(name = "session-id", defaultValue = "") String sessionId) {
        try {

            // get the Username - throws Unauthorized
            String username = sessionService.getUsernameById(sessionId);

            // perform the Get
            List<Game> games = gameService.findAllByUsername(username);

            // return successful
            return ResponseEntity.ok().body(JSONResponses.toJson("games", games));

        } catch (UnauthorizedException e) {

            // catch Unauthorized - return 401
            return ResponseEntity.status(401).body(JSONResponses.error(e.getMessage()));

        } catch (Exception e) {

            // catch generic Exception - return badRequest
            return ResponseEntity.badRequest().body(JSONResponses.error(e.getMessage()));

        }
    }

    @GetMapping("/{gameId}")
    public ResponseEntity<String> getGame(
            @CookieValue(name = "session-id", defaultValue = "") String sessionId,
            @PathVariable String gameId) {
        try {

            sessionService.validateSessionId(sessionId);

            Game game = gameService.findById(UUID.fromString(gameId));

            if (game == null) {
                throw new NotFoundException("Game", "ID: " + gameId);
            }

            return ResponseEntity.ok().body(JSONResponses.toJson("game", game));

        } catch (UnauthorizedException e) {

            return ResponseEntity.status(401).body(JSONResponses.unauthorized());

        } catch (NotFoundException e) {

            return ResponseEntity.status(404).body(JSONResponses.error(e.getMessage()));

        } catch (Exception e) {

            return ResponseEntity.badRequest().body(JSONResponses.error(e.getMessage()));

        }

    }

    /**
     * Get the valid moves
     * 
     * @param sessionId
     * @param startingSquare
     * @param gameId
     * @return
     */
    @GetMapping("/{gameId}/validMoves")
    public ResponseEntity<String> getValidMoves(
            @CookieValue(name = "session-id", defaultValue = "") String sessionId,
            @RequestParam(required = false) int[] startingSquare,
            @RequestParam(required = false) String playerColor,
            @PathVariable String gameId) {
        try {

            // Get the username - throws unauthorized
            String username = sessionService.getUsernameById(sessionId);

            // Get the validMoves for the given Game and options
            List<String> moves = gameService.getValidMoves(username, gameId, Optional.ofNullable(startingSquare),
                    Optional.ofNullable(playerColor));

            // Return successful
            return ResponseEntity.ok().body(JSONResponses.toJson("validMoves", moves));

        } catch (UnauthorizedException e) {

            // Catch Unauthorized - return 401
            return ResponseEntity.status(401).build();

        } catch (NotFoundException e) {

            // Catch NotFound - return 404
            return ResponseEntity.notFound().build();

        } catch (Exception e) {

            // Catch Generic Exception - return BadRequest
            return ResponseEntity.badRequest().body(JSONResponses.error(e.getMessage()));

        }
    }

    @GetMapping("/board/validMoves")
    public ResponseEntity<String> getValidMovesFromFen(
            @CookieValue(name = "session-id", defaultValue = "") String sessionId,
            @RequestParam(required = false) int[] startingSquare,
            @RequestParam(required = false) String playerColor,
            @RequestParam String fen) {
        try {

            sessionService.validateSessionId(sessionId);

            List<String> moves = gameService.getValidMoves(fen, Optional.ofNullable(startingSquare),
                    Optional.ofNullable(playerColor));

            // Return successful
            return ResponseEntity.ok().body(JSONResponses.toJson("validMoves", moves));

        } catch (UnauthorizedException e) {

            // Catch Unauthorized - return 401
            return ResponseEntity.status(401).build();

        } catch (Exception e) {

            // Catch Generic Exception - return BadRequest
            return ResponseEntity.badRequest().body(JSONResponses.error(e.getMessage()));

        }
    }

    @PutMapping("/{gameId}/move")
    public ResponseEntity<String> doMove(
            @CookieValue(name = "session-id", defaultValue = "") String sessionId,
            @RequestBody MoveDTO move,
            @PathVariable String gameId) {
        try {

            // Get Username - throws Unauthorized
            String username = sessionService.getUsernameById(sessionId);

            // Attempt to do the move
            gameService.doMove(username, gameId, move);

            // Return successful
            return ResponseEntity.ok().body(JSONResponses.success());

        } catch (UnauthorizedException e) {

            // Catch Unauthorized - return 401
            return ResponseEntity.status(401).body(JSONResponses.unauthorized());

        } catch (NotFoundException e) {

            // Catch Not Found - return 404
            return ResponseEntity.status(404).body(JSONResponses.unauthorized());

        } catch (Exception e) {

            // Catch Generic Exception - return BadRequest
            return ResponseEntity.badRequest().body(JSONResponses.error(e.getMessage()));

        }
    }
}
