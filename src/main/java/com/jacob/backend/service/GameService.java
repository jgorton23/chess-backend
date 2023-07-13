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

        if (startingSquare.isPresent()) {
            startingSquareList.add(startingSquare.get());
        } else {
            for (int y = 0; y < grid.length; y++) {
                for (int x = 0; x < grid[0].length; x++) {
                    if (grid[y][x].equals(" ")) {
                        continue;
                    } else if (!playerColor.isPresent()) {
                        startingSquareList.add(new int[] { x, y });
                    } else if ((grid[y][x].equals(grid[y][x].toUpperCase()) && playerColor.get().equals("w"))
                            || (grid[y][x].equals(grid[y][x].toLowerCase()) && playerColor.get().equals("b"))) {
                        startingSquareList.add(new int[] { x, y });
                    }
                }
            }
        }

        // List of all possible moves
        List<String> moves = new ArrayList<String>();

        for (int[] start : startingSquareList) {
            List<String> pieceMoves = findValidMoves(grid, start);
            moves.addAll(pieceMoves);
        }

        return moves;
    }

    // #region private helper

    // #region findValidMoves

    private List<String> findValidMoves(String[][] grid, int[] start) {
        int x = start[0], y = start[1];

        List<String> validMoves = new ArrayList<String>();

        switch (grid[y][x]) {
            case "R", "r":
                validMoves = findValidRookMoves(grid, start);
                break;
            case "N", "n":
                validMoves = findValidKnightMoves(grid, start);
                break;
            case "B", "b":
                validMoves = findValidBishopMoves(grid, start);
                break;
            case "K", "k":
                validMoves = findValidKingMoves(grid, start);
                break;
            case "Q", "q":
                validMoves = findValidQueenMoves(grid, start);
                break;
            case "P", "p":
                validMoves = findValidPawnMoves(grid, start);
                break;
        }

        return validMoves;
    }

    private List<String> findValidRookMoves(String[][] grid, int[] start) {

        return null;
    }

    private List<String> findValidKnightMoves(String[][] grid, int[] start) {
        return null;
    }

    private List<String> findValidBishopMoves(String[][] grid, int[] start) {
        return null;
    }

    private List<String> findValidKingMoves(String[][] grid, int[] start) {
        return null;
    }

    private List<String> findValidQueenMoves(String[][] grid, int[] start) {
        List<String> result = new ArrayList<String>();
        result.addAll(findValidRookMoves(grid, start));
        result.addAll(findValidBishopMoves(grid, start));
        return result;
    }

    private List<String> findValidPawnMoves(String[][] grid, int[] start) {
        return null;
    }

    // #endregion

    private boolean isAttacked(String[][] grid, int[] piece) {
        return false;
    }

    private String[][] FENToGrid(String FEN) {
        // in order to allow for bigger board sizes this needs to be revised
        for (int index = 1; index <= 9; index++) {
            FEN.replace(Integer.toString(index), " ".repeat(index));
        }
        return Arrays.stream(FEN.split("/")).map(row -> row.split("")).toArray(String[][]::new);
    }

    // #endregion

}
