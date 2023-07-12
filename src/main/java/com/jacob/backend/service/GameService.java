package com.jacob.backend.service;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.jacob.backend.data.Model.Game;
import com.jacob.backend.data.Model.User;
import com.jacob.backend.repository.interfaces.GameRepositoryInterface;
import com.jacob.backend.responses.exceptions.MissingFieldException;
import com.jacob.backend.responses.exceptions.NotFoundException;

@Service
public class GameService {

    @Autowired
    private GameRepositoryInterface gameRepo;

    @Autowired
    private UserService userService;

    public Game findById(UUID gameId) {
        return gameRepo.getById(gameId);
    }

    public List<Game> findAllByUserId(UUID userId) {
        return gameRepo.getAllByUserId(userId);
    }

    public List<Game> findAllByUsername(String username) {
        User u = userService.findByUsername(username);
        if (u == null) {
            throw new NotFoundException("User", "username: " + username);
        }
        return findAllByUserId(u.getId());
    }

    public String create(Game game) {
        String whitePlayerUsername = game.getWhitePlayerUsername();
        if (whitePlayerUsername == null) {
            throw new MissingFieldException("New Game Form", "White Player Username");
        }
        String blackPlayerUsername = game.getBlackPlayerUsername();
        if (blackPlayerUsername == null) {
            throw new MissingFieldException("New Game Form", "Black Player Username");
        }
        User whitePlayer = userService.findByUsername(whitePlayerUsername);
        if (whitePlayer == null) {
            throw new NotFoundException("User", "Username: " + whitePlayerUsername);
        }
        User blackPlayer = userService.findByUsername(blackPlayerUsername);
        if (blackPlayer == null) {
            throw new NotFoundException("User", "Username: " + blackPlayerUsername);
        }
        game.setBlackPlayerId(blackPlayer.getId());
        game.setWhitePlayerId(whitePlayer.getId());
        gameRepo.save(game);
        return game.getId();
    }

    public void update(Game game) {
        gameRepo.update(game);
    }

    public List<String> getValidMoves(String gameId, Optional<int[]> startingSquare, Optional<String> playerColor) {

        // Get the UUID of the Game
        // TODO: check for valid UUID
        UUID id = UUID.fromString(gameId);

        // Get the game by the UUID
        Game game = findById(id);
        if (game == null) {
            throw new NotFoundException("Game", String.format("GameId: %s", gameId));
        }

        // Get the board from the game
        String FEN = game.getFEN();

        // Create a grid to store the piece occupying each square on the board
        String[][] grid = FENToGrid(FEN);

        // Create a list of all possible starting squares
        List<int[]> startingSquareList = new ArrayList<int[]>();

        // List of all possible moves
        List<String> moves = new ArrayList<String>();

        return moves;
    }

    private String[][] FENToGrid(String FEN) {
        // in order to allow for bigger board sizes this needs to be revised
        for (int index = 1; index <= 9; index++) {
            FEN.replace(Integer.toString(index), " ".repeat(index));
        }
        return Arrays.stream(FEN.split("/")).map(row -> row.split("")).toArray(String[][]::new);
    }
}
