package com.jacob.backend.service;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.jacob.backend.data.DTO.MoveDTO;
import com.jacob.backend.data.Model.Game;
import com.jacob.backend.data.Model.User;
import com.jacob.backend.repository.interfaces.GameRepositoryInterface;
import com.jacob.backend.responses.exceptions.MissingFieldException;
import com.jacob.backend.responses.exceptions.NotFoundException;
import com.jacob.backend.responses.exceptions.UnauthorizedException;

/**
 * Service containing Game related logic
 */
@Service
public class GameService {

    /**
     * Repo for Game persistence
     */
    @Autowired
    private GameRepositoryInterface gameRepo;

    /**
     * Service for User related logic
     */
    @Autowired
    private UserService userService;

    /**
     * Service containing session related logic
     * Only needed for UUID validation - move to HelperService someday
     */
    @Autowired
    private SessionService sessionService;

    // #region CRUD

    /**
     * Gets the Game with the given UUID
     * 
     * @param gameId the UUID of the Game to get
     * @return the Game with the given UUID
     */
    public Game findById(UUID gameId) {

        // Find and return the Game with the given UUID
        return gameRepo.getById(gameId);

    }

    /**
     * Gets all Games for the user with the given UUID
     * 
     * @param userId the UUID of the user for which to get Games
     * @return a list of the Users Games
     */
    public List<Game> findAllByUserId(UUID userId) {

        // Find and return all Games where one of the players UUIDs is the given UUID
        return gameRepo.getAllByUserId(userId);

    }

    /**
     * Gets all Games for the User with the given Username
     * 
     * @param username the Username of the user for which to get Games
     * @return a list of the Users Gaems
     */
    public List<Game> findAllByUsername(String username) {

        // Get the User with the given Username
        User u = userService.findByUsername(username);

        // Ensure the User was found
        if (u == null) {
            throw new NotFoundException("User", "username: " + username);
        }

        // Return find the Users Games by User UUID
        return findAllByUserId(u.getId());

    }

    /**
     * Creates a new Game and persists it in the database
     * 
     * @param game the Game to create
     * @return the newly created Games UUID
     */
    public String create(String username, Game game) {

        // Get the whitePlayerUsername
        String whitePlayerUsername = game.getWhitePlayerUsername();
        if (whitePlayerUsername == null) {
            throw new MissingFieldException("New Game Form", "White Player Username");
        }

        // Get the blackPlayerUsername
        String blackPlayerUsername = game.getBlackPlayerUsername();
        if (blackPlayerUsername == null) {
            throw new MissingFieldException("New Game Form", "Black Player Username");
        }

        // Ensure the User creating the Game is one of the players
        if (!whitePlayerUsername.equals(username) && !blackPlayerUsername.equals(username)) {
            throw new UnauthorizedException();
        }

        // Ensure the whitePlayer exists
        User whitePlayer = userService.findByUsername(whitePlayerUsername);
        if (whitePlayer == null) {
            throw new NotFoundException("User", "Username: " + whitePlayerUsername);
        }

        // Ensure the blackPlayer exists
        User blackPlayer = userService.findByUsername(blackPlayerUsername);
        if (blackPlayer == null) {
            throw new NotFoundException("User", "Username: " + blackPlayerUsername);
        }

        // Set default values
        game.setBlackPlayerId(blackPlayer.getId());
        game.setWhitePlayerId(whitePlayer.getId());
        game.setResult("*");
        game.setMoveTimes("");
        game.setMoves("");

        gameRepo.save(game);

        return game.getId();

    }

    /**
     * Updates the given Game in the database
     * 
     * @param game the Game to update
     */
    public void update(String username, Game game) {

        // Ensure the User updating the Game is one of the players
        if (username.equals(game.getBlackPlayerUsername()) && !username.equals(game.getWhitePlayerUsername())) {
            throw new UnauthorizedException();
        }

        // Update the Game
        gameRepo.update(game);

    }

    /**
     * performs a given move on a given Game
     * 
     * @param username the Username of the User performing the move
     * @param gameId   the UUID of the Game on which to perform the move
     * @param move     the move to perform
     */
    public void doMove(String username, String gameId, MoveDTO move) {

        // Ensure the given gameId is a valid UUID
        if (!sessionService.isValidUUID(gameId)) {
            throw new NotFoundException("Game", "ID: " + gameId);
        }

        // Get the Game with the given UUID
        Game game = findById(UUID.fromString(gameId));

        // Ensure the Game exists
        if (game == null) {
            throw new NotFoundException("Game", "ID: " + gameId);
        }

        // Ensure the User doing the move is one of the players
        if (!username.equals(game.getBlackPlayerUsername()) && !username.equals(game.getWhitePlayerUsername())) {
            throw new UnauthorizedException();
        }

        // Get all possible moves
        List<String> validMoves = getValidMoves(username, gameId, Optional.ofNullable(null), Optional.ofNullable(null));

        // Ensure the attempted move is valid
        if (!validMoves.contains(move.toString())) {
            throw new RuntimeException("Attempting to perform an Invalid Move");
        }

        // Get the grid to perform the move on
        String[][] grid = FENToGrid(game.getFEN());

        int[] start = move.getStartSquare();
        int[] end = move.getDestSquare();

        // Perform the move on the Grid
        grid[end[1]][end[0]] = grid[start[1]][start[0]];
        grid[start[1]][start[0]] = " ";

        // Set the Games FEN to match the board state after the move
        game.setFEN(gridToFEN(grid));

        // Add the move to the list of mvoes
        game.setMoves(game.getMoves() + " " + move.toString());

        // game.setMoveTimes(game.getMoveTimes() + " " + move.getMiliseconds());

        // game.setResult(); TODO fill in result

        // Update the Game in the db
        update(username, game);

    }

    /**
     * Gets all the valid moves for the curent position of the Game with the given
     * UUID
     * 
     * @param gameId         the UUID of the Game to get valid moves for
     * @param startingSquare the optional (x, y) coordinates, with the origin at the
     *                       top-left, of the square to find valid moves from
     * @param playerColor    the optional player color to find valid moves for
     * @return a list of valid moves in modified SAN
     */
    public List<String> getValidMoves(String username, String gameId, Optional<int[]> startingSquare,
            Optional<String> playerColor) {

        // Get the UUID of the Game
        if (!sessionService.isValidUUID(gameId)) {
            throw new NotFoundException("Game", "ID: " + gameId);
        }

        // Get the Game with the given the UUID
        Game game = findById(UUID.fromString(gameId));

        // Ensure the Game exists
        if (game == null) {
            throw new NotFoundException("Game", String.format("GameId: %s", gameId));
        }

        // Ensure the User making the request is one of the players
        if (!username.equals(game.getBlackPlayerUsername()) && !username.equals(game.getWhitePlayerUsername())) {
            throw new UnauthorizedException();
        }

        // Get the board from the game
        String FEN = game.getFEN();

        // Create a grid to store the piece occupying each square on the board
        String[][] grid = FENToGrid(FEN);

        // Create a list of all possible starting squares
        List<int[]> startingSquareList = new ArrayList<int[]>();

        // Add only the relevant starting squares
        if (startingSquare.isPresent()) {
            startingSquareList.add(startingSquare.get());
        } else if (playerColor.isPresent()) {
            startingSquareList.addAll(findPlayerPieces(grid, playerColor.get()));
        } else {
            startingSquareList.addAll(findPlayerPieces(grid, "w"));
            startingSquareList.addAll(findPlayerPieces(grid, "b"));
        }

        // List of all possible moves
        List<String> moves = new ArrayList<String>();

        // Add the moves from each starting position
        for (int[] start : startingSquareList) {
            List<String> pieceMoves = findValidPieceMoves(grid, start, false);
            moves.addAll(pieceMoves);
        }

        return moves;
    }

    // #endregion

    // #region private helper

    // #region findValidMoves

    private List<String> findValidPieceMoves(String[][] grid, int[] start, boolean ignoreCheck) {
        int x = start[0], y = start[1];

        List<String> validMoves = new ArrayList<String>();

        switch (grid[y][x]) {
            case "R", "r":
                validMoves = findValidRookMoves(grid, start, ignoreCheck);
                break;
            case "N", "n":
                validMoves = findValidKnightMoves(grid, start, ignoreCheck);
                break;
            case "B", "b":
                validMoves = findValidBishopMoves(grid, start, ignoreCheck);
                break;
            case "K", "k":
                validMoves = findValidKingMoves(grid, start, ignoreCheck);
                break;
            case "Q", "q":
                validMoves = findValidQueenMoves(grid, start, ignoreCheck);
                break;
            case "P", "p":
                validMoves = findValidPawnMoves(grid, start, ignoreCheck);
                break;
        }

        return validMoves;
    }

    private List<String> findValidRookMoves(String[][] grid, int[] start, boolean ignoreCheck) {

        int x = start[0], y = start[1];

        List<String> movesList = new ArrayList<String>();

        for (int[] dir : new int[][] {
                new int[] { 0, 1 }, // horizontal, vertical increment
                new int[] { 0, -1 },
                new int[] { 1, 0 },
                new int[] { -1, 0 } }) {

            int x2 = x + dir[0], y2 = y + dir[1];
            String[][] gridAfterMove = Arrays.stream(grid).map(row -> row.clone()).toArray(String[][]::new);
            String playerColor = grid[y][x].equals(grid[y][x].toLowerCase()) ? "b" : "w";

            while (0 <= x2 && x2 < grid[0].length && 0 <= y2 && y2 < grid.length) {
                // if this square is the same color as the rook, break while
                if (isSameColorPiece(grid, x, y, x2, y2)) {
                    break;
                }

                // update the pieces to resemble the attempted move
                gridAfterMove[y2][x2] = gridAfterMove[y][x];
                gridAfterMove[y2 - dir[1]][x2 - dir[0]] = " ";

                // if moving to this square leaves the king checked, skip while iteration
                if (!ignoreCheck && isInCheck(gridAfterMove, playerColor)) {
                    continue;
                }

                movesList.add(
                        grid[y][x] + (char) (x + 'a') + (Math.abs(y - 8)) + (char) (x2 + 'a') + (Math.abs(y2 - 8)));

                // if this square is not empty, it must be an opposing piece that we capture
                if (!grid[y2][x2].equals(" ")) {
                    break;
                }

                x2 += dir[0];
                y2 += dir[1];
            }
        }

        return movesList;

    }

    private List<String> findValidKnightMoves(String[][] grid, int[] start, boolean ignoreCheck) {

        int x = start[0], y = start[1];

        List<String> movesList = new ArrayList<String>();

        String[][] gridAfterMove = Arrays.stream(grid).map(row -> row.clone()).toArray(String[][]::new);

        String playerColor = grid[y][x].equals(grid[y][x].toLowerCase()) ? "b" : "w";

        for (int[] dir : new int[][] {
                new int[] { -1, 2 },
                new int[] { -1, -2 },
                new int[] { -2, 1 },
                new int[] { -2, -1 },
                new int[] { 1, 2 },
                new int[] { 1, -2 },
                new int[] { 2, 1 },
                new int[] { 2, -1 } }) {

            int x2 = x + dir[0], y2 = y + dir[1];

            if (0 > x2 || x2 >= grid[0].length || 0 > y2 || y2 >= grid.length) {
                continue;
            }

            if (isSameColorPiece(grid, x, y, x2, y2)) {
                continue;
            }

            // simulate move
            gridAfterMove[y][x] = " ";
            gridAfterMove[y2][x2] = grid[y][x];

            // if moving to this square leaves the king checked, skip while iteration
            if (!ignoreCheck && isInCheck(gridAfterMove, playerColor)) {
                continue;
            }

            movesList.add(grid[y][x] + (char) (x + 'a') + (Math.abs(y - 8)) + (char) (x2 + 'a') + (Math.abs(y2 - 8)));

            // return grid to regular state, ready for next move
            gridAfterMove[y][x] = grid[y][x];
            gridAfterMove[y2][x2] = grid[y2][x2];

            // if this square is not empty, it must be an opposing piece that we capture
            if (!grid[y2][x2].equals(" ")) {
                break;
            }

        }

        return movesList;

    }

    private List<String> findValidBishopMoves(String[][] grid, int[] start, boolean ignoreCheck) {

        int x = start[0], y = start[1];

        List<String> movesList = new ArrayList<String>();

        String playerColor = grid[y][x].equals(grid[y][x].toLowerCase()) ? "b" : "w";

        for (int[] dir : new int[][] {
                new int[] { 1, 1 },
                new int[] { -1, -1 },
                new int[] { 1, -1 },
                new int[] { -1, 1 } }) {

            String[][] gridAfterMove = Arrays.stream(grid).map(row -> row.clone()).toArray(String[][]::new);
            int x2 = x + dir[0], y2 = y + dir[1];

            while (0 <= x2 && x2 < grid[0].length && 0 <= y2 && y2 < grid.length) {
                // if this square is the same color as the rook, break while
                if (isSameColorPiece(grid, x, y, x2, y2)) {
                    break;
                }

                // update the pieces to resemble the attempted move
                gridAfterMove[y2][x2] = gridAfterMove[y][x];
                gridAfterMove[y2 - dir[1]][x2 - dir[0]] = " ";

                // if moving to this square leaves the king checked, skip while iteration
                if (!ignoreCheck && isInCheck(gridAfterMove, playerColor)) {
                    continue;
                }

                movesList.add(
                        grid[y][x] + (char) (x + 'a') + (Math.abs(y - 8)) + (char) (x2 + 'a') + (Math.abs(y2 - 8)));

                // if this square is not empty, it must be an opposing piece that we capture
                if (!grid[y2][x2].equals(" ")) {
                    break;
                }

                x2 += dir[0];
                y2 += dir[1];
            }

            // TODO: this could be different depending on why the while loop terminated
            // gridAfterMove[y2 - dir[1]][x2 - dir[0]] = grid[y2 - dir[1]][x2 - dir[0]];

            // gridAfterMove[y][x] = grid[y][x];

        }

        return movesList;

    }

    private List<String> findValidKingMoves(String[][] grid, int[] start, boolean ignoreCheck) {

        int x = start[0], y = start[1];

        List<String> movesList = new ArrayList<String>();

        String[][] gridAfterMove = Arrays.stream(grid).map(row -> row.clone()).toArray(String[][]::new);

        String playerColor = grid[y][x].equals(grid[y][x].toLowerCase()) ? "b" : "w";

        for (int[] dir : new int[][] {
                new int[] { 1, 1 },
                new int[] { -1, -1 },
                new int[] { 1, -1 },
                new int[] { -1, 1 },
                new int[] { -1, 0 },
                new int[] { 0, -1 },
                new int[] { 1, 0 },
                new int[] { 0, 1 } }) {

            int x2 = x + dir[0], y2 = y + dir[1];

            if (0 > x2 || x2 >= grid[0].length || 0 > y2 || y2 >= grid.length) {
                continue;
            }

            if (isSameColorPiece(grid, x, y, x2, y2)) {
                continue;
            }

            // simulate move
            gridAfterMove[y][x] = " ";
            gridAfterMove[y2][x2] = grid[y][x];

            // if moving to this square leaves the king checked, skip while iteration
            if (!ignoreCheck && isInCheck(gridAfterMove, playerColor)) {
                continue;
            }

            movesList.add(grid[y][x] + (char) (x + 'a') + (Math.abs(y - 8)) + (char) (x2 + 'a') + (Math.abs(y2 - 8)));

            // return grid to regular state, ready for next move
            gridAfterMove[y][x] = grid[y][x];
            gridAfterMove[y2][x2] = grid[y2][x2];

            // if this square is not empty, it must be an opposing piece that we capture
            if (!grid[y2][x2].equals(" ")) {
                break;
            }

        }

        return movesList;

    }

    private List<String> findValidQueenMoves(String[][] grid, int[] start, boolean ignoreCheck) {
        List<String> result = new ArrayList<String>();
        result.addAll(findValidRookMoves(grid, start, ignoreCheck));
        result.addAll(findValidBishopMoves(grid, start, ignoreCheck));
        return result;
    }

    private List<String> findValidPawnMoves(String[][] grid, int[] start, boolean ignoreCheck) {

        int x = start[0], y = start[1];

        List<String> movesList = new ArrayList<String>();

        String[][] gridAfterMove = Arrays.stream(grid).map(row -> row.clone()).toArray(String[][]::new);

        String playerColor = grid[y][x].equals(grid[y][x].toLowerCase()) ? "b" : "w";

        int[] increment = playerColor.equals("w") ? new int[] { 0, -1 } : new int[] { 0, 1 };

        for (int[] dir : new int[][] {
                new int[] { 1, 1 * increment[1] },
                new int[] { -1, 1 * increment[1] },
                new int[] { 0, 1 * increment[1] },
                new int[] { 0, 2 * increment[1] } }) {

            int x2 = x + dir[0], y2 = y + dir[1];

            // If the dest is out of bounds
            if (0 > x2 || x2 >= grid[0].length || 0 > y2 || y2 >= grid.length) {
                continue;
            }

            // If the dest is occupied by another piece of the same color
            if (isSameColorPiece(grid, x, y, x2, y2)) {
                continue;
            }

            // If the move is diagonal, but the dest is empty
            if (dir[0] != 0 && grid[y2][x2].equals(" ")) {
                continue;
            }

            // If the move is forward 2, but the pawn isn't on its starting rank
            if (dir[1] > 1 && !(0 > y - (2 * increment[1]) || y - (2 * increment[1]) >= grid.length)) {
                continue;
            }

            gridAfterMove[y2][x2] = grid[y][x];
            gridAfterMove[y][x] = " ";

            // If the move leaves the king in check
            if (!ignoreCheck && isInCheck(gridAfterMove, playerColor)) {
                continue;
            }

            movesList.add(grid[y][x] + (char) (x + 'a') + (Math.abs(y - 8)) + (char) (x2 + 'a') + (Math.abs(y2 - 8)));

            gridAfterMove[y][x] = grid[y][x];
            gridAfterMove[y2][x2] = grid[y2][x2];

        }

        return movesList;

    }

    // #endregion

    private boolean isInCheck(String[][] grid, String playerColor) {

        List<int[]> opponentPieces = findPlayerPieces(grid, playerColor.equals("w") ? "b" : "w");

        List<String> opponentValidMoves = new ArrayList<String>();

        for (int[] opponentPiece : opponentPieces) {
            opponentValidMoves.addAll(findValidPieceMoves(grid, opponentPiece, true));
        }

        String kingLocation = "";

        for (int i = 0; i < grid.length; i++) {
            for (int j = 0; j < grid[0].length; j++) {
                if (playerColor.equals("w") && grid[i][j].equals(grid[i][j].toUpperCase())) {
                    kingLocation = "" + (char) (j + 'a') + (char) (i + 'a');
                }
                if (playerColor.equals("b") && grid[i][j].equals(grid[i][j].toLowerCase())) {
                    kingLocation = "" + (char) (j + 'a') + (char) (i + 'a');
                }
            }
        }

        for (String move : opponentValidMoves) {
            if (move.endsWith(kingLocation)) {
                return true;
            }
        }
        return false;
    }

    private List<int[]> findPlayerPieces(String[][] grid, String playerColor) {

        List<int[]> pieces = new ArrayList<int[]>();

        for (int y = 0; y < grid.length; y++) {
            for (int x = 0; x < grid[0].length; x++) {
                if (grid[y][x].equals(" ")) {
                    continue;
                } else if ((grid[y][x].equals(grid[y][x].toUpperCase()) && playerColor.equals("w"))
                        || (grid[y][x].equals(grid[y][x].toLowerCase()) && playerColor.equals("b"))) {
                    pieces.add(new int[] { x, y });
                }
            }
        }

        return pieces;

    }

    private String[][] FENToGrid(String FEN) {
        // in order to allow for bigger board sizes this needs to be revised
        for (int index = 1; index <= 9; index++) {
            FEN = FEN.replace(Integer.toString(index), " ".repeat(index));
        }
        return Arrays.stream(FEN.split("/")).map(row -> row.split("")).toArray(String[][]::new);
    }

    private String gridToFEN(String[][] grid) {
        String result = "";
        for (String[] row : grid) {
            String rowFEN = String.join("", row);
            for (int i = 8; i >= 1; i--) {
                rowFEN = rowFEN.replace(" ".repeat(i), "" + i);
            }
            result += rowFEN;
            result += "/";
        }
        return result;
    }

    private boolean isSameColorPiece(String[][] grid, int x1, int y1, int x2, int y2) {
        return (("RNBKQP".contains(grid[y1][x1]) && "RNBKQP".contains(grid[y2][x2]))
                || ("rnbkqp".contains(grid[y1][x1]) && "rnbkqp".contains(grid[y2][x2])));
    }

    // #endregion

}
