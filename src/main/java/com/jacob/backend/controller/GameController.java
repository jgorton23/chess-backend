package com.jacob.backend.controller;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.jacob.backend.data.Model.*;
import com.jacob.backend.responses.JSONResponses;
import com.jacob.backend.responses.exceptions.NotFoundException;
import com.jacob.backend.responses.exceptions.UnauthorizedException;
import com.jacob.backend.service.GameService;
import com.jacob.backend.service.SessionService;

import lombok.extern.apachecommons.CommonsLog;

/**
 * Controller containing endpoints related Games
 */
@RestController
@CrossOrigin(origins = "http://localhost:4200", allowCredentials = "true")
@RequestMapping("/games")
@CommonsLog
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

            // log the method start
            log.info(String.format("HTTP request received | URL: '%s', Method: '%s', Data: '%s'",
                    "/games/new",
                    "POST",
                    JSONResponses.toJson(game)));

            // get the Username - throws Unauthorized
            String username = sessionService.getUsernameById(sessionId);

            // make sure the current User is one of the game Players
            if (!username.equals(game.getBlackPlayerUsername()) && !username.equals(game.getWhitePlayerUsername())) {
                throw new UnauthorizedException("current User must be one of the Game players");
            }

            // perform Create
            String gameId = gameService.create(username, game);

            // log the successful resoponse
            log.info(String.format("HTTP response sent | Data: '%s'", JSONResponses.toJson(gameId)));

            // return gameId
            return ResponseEntity.ok().body(JSONResponses.toJson("gameId", gameId));

        } catch (UnauthorizedException e) {

            // log the exception
            log.error("Failed to create a new 'Game'", e);

            // catch Unauthorized - return 401
            return ResponseEntity.status(401).body(JSONResponses.unauthorized());

        } catch (Exception e) {

            // log the exception
            log.error("Failed to create a new 'Game'", e);

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

            // log the method start
            log.info(String.format("HTTP request received | URL: '%s', Method: '%s', Data: '%s'", "/games", "GET", ""));

            // get the Username - throws Unauthorized
            String username = sessionService.getUsernameById(sessionId);

            // perform the Get
            List<Game> games = gameService.findAllByUsername(username);

            // log the successful response
            log.info(String.format("HTTP response sent | Data: '%s'", JSONResponses.toJson(games)));

            // return successful
            return ResponseEntity.ok().body(JSONResponses.toJson("games", games));

        } catch (UnauthorizedException e) {

            // log the exception
            log.error("Failed to get 'Games'", e);

            // catch Unauthorized - return 401
            return ResponseEntity.status(401).body(JSONResponses.error(e.getMessage()));

        } catch (Exception e) {

            // log the exception
            log.error("Failed to get 'Games'", e);

            // catch generic Exception - return badRequest
            return ResponseEntity.badRequest().body(JSONResponses.error(e.getMessage()));

        }
    }

    /**
     * Gets the game with the given UUID
     * 
     * @param sessionId the session if of the user who is making the request
     * @param gameId    the id of the game being requested
     * @return the found Game if one exists, otherwise an exception
     */
    @GetMapping("/{gameId}")
    public ResponseEntity<String> getGame(
            @CookieValue(name = "session-id", defaultValue = "") String sessionId,
            @PathVariable String gameId) {
        try {

            // log the request start
            log.info(String.format("HTTP request received | URL: '%s', Method: '%s', Data: '%s'",
                    "/games/" + gameId,
                    "GET", ""));

            // validate the session id
            sessionService.validateSessionId(sessionId);

            // get the game with the given UUID
            Game game = gameService.findById(UUID.fromString(gameId));

            // throw an exception if not found
            if (game == null) {
                throw new NotFoundException("Game", "ID: " + gameId);
            }

            // log the successful response
            log.info(String.format("HTTP response sent | Data: '%s'", JSONResponses.toJson(game)));

            // return successful
            return ResponseEntity.ok().body(JSONResponses.toJson("game", game));

        } catch (UnauthorizedException e) {

            // log the exception
            log.error("Failed to get 'Game'", e);

            // catch unauthorized, return 401
            return ResponseEntity.status(401).body(JSONResponses.unauthorized());

        } catch (NotFoundException e) {

            // log the exception
            log.error("Failed to get 'Game'", e);

            // catch NotFound, return 404
            return ResponseEntity.status(404).body(JSONResponses.error(e.getMessage()));

        } catch (Exception e) {

            // log the exception
            log.error("Failed to get 'Game'", e);

            // catch generic exception, return bad request
            return ResponseEntity.badRequest().body(JSONResponses.error(e.getMessage()));

        }

    }

    /**
     * Get the current valid moves for the game with the given UUID
     * 
     * @param sessionId      the session id of the user making the request
     * @param startingSquare the optional starting square to filter the moves by
     * @param playerColor    the optional player color to filter the moves by
     * @param gameId         the UUID of the game for which to get valid moves
     * @return a list of valid moves for the given game
     */
    @GetMapping("/{gameId}/validMoves")
    public ResponseEntity<String> getValidMoves(
            @CookieValue(name = "session-id", defaultValue = "") String sessionId,
            @RequestParam(required = false) int[] startingSquare,
            @RequestParam(required = false) String playerColor,
            @PathVariable String gameId) {
        try {

            // log the request start
            log.info(String.format("HTTP request received | URL: '%s', Method: '%s', Data: '%s'",
                    "/games/" + gameId + "/validMoves",
                    "GET",
                    JSONResponses.toJson(startingSquare) + " " + playerColor));

            // Get the username - throws unauthorized
            String username = sessionService.getUsernameById(sessionId);

            // Get the validMoves for the given Game and options
            List<String> moves = gameService.getValidMoves(username, gameId, Optional.ofNullable(startingSquare),
                    Optional.ofNullable(playerColor));

            // log the successful response
            log.info(String.format("HTTP response sent | Data: '%s'", JSONResponses.toJson(moves)));

            // Return successful
            return ResponseEntity.ok().body(JSONResponses.toJson("validMoves", moves));

        } catch (UnauthorizedException e) {

            // log the exception
            log.error("Failed to get valid moves", e);

            // Catch Unauthorized - return 401
            return ResponseEntity.status(401).build();

        } catch (NotFoundException e) {

            // log the exception
            log.error("Failed to get valid moves", e);

            // Catch NotFound - return 404
            return ResponseEntity.notFound().build();

        } catch (Exception e) {

            // log the exception
            log.error("Failed to get valid moves", e);

            // Catch Generic Exception - return BadRequest
            return ResponseEntity.badRequest().body(JSONResponses.error(e.getMessage()));

        }
    }

    /**
     * Get the game state history for the game with the given UUID
     * 
     * @param sessionId the sessionId of the user who made the request
     * @param gameId    the UUID of the game for which to return previous game
     *                  states
     * @return a list of Games, representing the state of the game after each move
     */
    @GetMapping("/{gameId}/states")
    public ResponseEntity<String> getGameStates(
            @CookieValue(name = "session-id", defaultValue = "") String sessionId,
            @PathVariable String gameId) {

        try {

            // log the request start
            log.info(String.format("HTTP request received | URL: '%s', Method: '%s'",
                    "/games/" + gameId + "/states",
                    "GET"));

            // get the game states
            List<Game> gameStates = gameService.getGameStates(gameId);

            // log the successful response
            log.info(String.format("HTTP response sent | Data: '%s'", JSONResponses.toJson(gameStates)));

            // return successful
            return ResponseEntity.ok().body(JSONResponses.toJson("gameStates", gameStates));

        } catch (UnauthorizedException e) {

            // log the exception
            log.error("Failed to get game states", e);

            // catch unauthorized, return 401
            return ResponseEntity.status(401).build();

        } catch (NotFoundException e) {

            // log the exception
            log.error("Failed to get game states", e);

            // catch not found, return 404
            return ResponseEntity.notFound().build();

        } catch (Exception e) {

            // log the exception
            log.error("Failed to get game states", e);

            // catch generic exception, return bad request
            return ResponseEntity.badRequest().body(JSONResponses.error(e.getMessage()));

        }
    }

    /**
     * Get the valid moves for the given board
     * 
     * @param sessionId      the session id of the user making the request
     * @param startingSquare the optional starting square on which to filter the
     *                       valid moves
     * @param playerColor    the optional player color on which to filter the valid
     *                       moves
     * @param fen            the FEN of the current game state, used to determine
     *                       what the valid moves are
     * @return a list of the valid moves for the given game state
     */
    @GetMapping("/board/validMoves")
    public ResponseEntity<String> getValidMovesFromFen(
            @CookieValue(name = "session-id", defaultValue = "") String sessionId,
            @RequestParam(required = false) int[] startingSquare,
            @RequestParam(required = false) String playerColor,
            @RequestParam String fen) {
        try {

            // log the request start
            log.info("HTTP request received | URL: '/games/board/validMoves', Method: 'GET'");

            // ensure the session is valid
            sessionService.validateSessionId(sessionId);

            // get the list of valid moves
            List<String> moves = gameService.getValidMoves(fen, Optional.ofNullable(null),
                    Optional.ofNullable(startingSquare), Optional.ofNullable(playerColor));

            // log the successful response
            log.info("HTTP response sent | Status: 200");

            // Return successful
            return ResponseEntity.ok().body(JSONResponses.toJson("validMoves", moves));

        } catch (UnauthorizedException e) {

            // log the exception
            log.error("Failed to get valid moves", e);

            // Catch Unauthorized - return 401
            return ResponseEntity.status(401).build();

        } catch (Exception e) {

            // log the exception
            log.error("Failed to get valid moves", e);

            // Catch Generic Exception - return BadRequest
            return ResponseEntity.badRequest().body(JSONResponses.error(e.getMessage()));

        }
    }
}
