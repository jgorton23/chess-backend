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
        if (!username.equals(game.getBlackPlayerUsername()) && !username.equals(game.getWhitePlayerUsername())) {
            throw new UnauthorizedException();
        }

        // Update the Game
        gameRepo.update(game);

    }

    /**
     * Updates the given Game to reflect the winner as the resigning player's
     * opponent
     * 
     * @param username the username of the player who is resigning
     * @param gameId   the game from which the user is resigning
     */
    public void resign(String username, UUID gameId) {

        // Get the Game with the given UUID
        Game game = findById(gameId);

        // Ensure the Game exists
        if (game == null) {
            throw new NotFoundException("Game", "ID: " + gameId);
        }

        if (!game.getResult().equals("*")) {
            throw new RuntimeException("Cannot resign from a game that has already ended");
        }

        String playerColor = "";
        // Ensure the User resigning is one of the players
        if (username.equals(game.getBlackPlayerUsername())) {
            playerColor = "b";
        } else if (username.equals(game.getWhitePlayerUsername())) {
            playerColor = "w";
        } else {
            throw new UnauthorizedException();
        }

        // Set the result of the game
        game.setResult(playerColor.equals("w") ? "0-1" : "1-0");

        // Update the game
        gameRepo.update(game);

    }

    /**
     * Updates the game to reflect the winner as the opponent of the player who ran
     * out of time
     * 
     * @param username the username of the player who ran out of time
     * @param gameId   the id of the game that the user timed out in
     */
    public void timeout(String username, UUID gameId) {

        Game game = findById(gameId);

        if (game == null) {
            throw new NotFoundException("Game", "ID: " + gameId);
        }

        if (!game.getResult().equals("*")) {
            throw new RuntimeException("Timeout Error: Game has already ended");
        }

        String playerColor = "";
        if (username.equals(game.getBlackPlayerUsername())) {
            playerColor = "b";
        } else if (username.equals(game.getWhitePlayerUsername())) {
            playerColor = "w";
        } else {
            throw new UnauthorizedException();
        }

        // Set the result of the game
        game.setResult(playerColor.equals("w") ? "0-1" : "1-0");

        // Update the game
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

        String playerColor = "";
        // Ensure the User doing the move is one of the players
        if (username.equals(game.getBlackPlayerUsername())) {
            playerColor = "b";
        } else if (username.equals(game.getWhitePlayerUsername())) {
            playerColor = "w";
        } else {
            throw new UnauthorizedException();
        }

        // Get all possible moves
        List<String> validMoves = getValidMoves(game, Optional.ofNullable(null), Optional.ofNullable(playerColor));

        // Ensure the attempted move is valid
        if (!validMoves.contains(move.toString())) {
            throw new RuntimeException("Attempting to perform an Invalid Move");
        }

        doMoveOnGame(game, move);

        // Update the Game in the db
        update(username, game);

    }

    public void doMoveOnGame(Game game, MoveDTO move) {

        String opponentColor = new String[] { "w", "b" }[game.getMoves().split(" ").length % 2];

        // Get the grid to perform the move on
        String[][] grid = FENToGrid(game.getFEN());

        int[] start = move.getStartSquare();
        int[] end = move.getDestSquare();

        // Perform the move on the Grid
        // if the move is an en passant
        if (move.getIsCapture() && move.toString().toLowerCase().contains("p") && grid[end[1]][end[0]].equals(" ")) {
            grid[start[1]][end[0]] = " ";
        }
        // if the move is a castle
        if (move.getPiece().toLowerCase().equals("k") && Math.abs(start[0] - end[0]) >= 2) {
            int[] rook = new int[2];
            rook[0] = end[0] < 4 ? 0 : 7;
            rook[1] = end[1];

            grid[end[1]][end[0] + (end[0] < 4 ? 1 : -1)] = grid[rook[1]][rook[0]];
            grid[rook[1]][rook[0]] = " ";
        }

        grid[end[1]][end[0]] = grid[start[1]][start[0]];
        grid[start[1]][start[0]] = " ";

        if (move.getPromotion() != null && move.getPiece().toLowerCase().equals("p") && end[1] % 7 == 0) {
            grid[end[1]][end[0]] = move.getPromotion();
            move.setIsCheck(isInCheck(grid, opponentColor));
            boolean hasNoMoves = isInMate(grid, opponentColor);
            move.setIsMate(move.getIsCheck() && hasNoMoves);
            move.setIsStalemate(!move.getIsCheck() && hasNoMoves);
        }

        // Set the new game features
        game.setFEN(gridToFEN(grid));
        game.setMoves((game.getMoves() + " " + move.toString()).trim());
        game.setMoveTimes((game.getMoveTimes() + " " + move.getMiliseconds()).trim());
        if (move.getIsMate()) {
            game.setResult(game.getMoves().split(" ").length % 2 == 0 ? "1-0" : "0-1");
        } else if (move.getIsStalemate()) {
            game.setResult("1/2-1/2");
        }

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
            throw new NotFoundException("Game", "ID: %s".formatted(gameId));
        }

        // Get the Game with the given the UUID
        Game game = findById(UUID.fromString(gameId));

        // Ensure the Game exists
        if (game == null) {
            throw new NotFoundException("Game", "ID: %s".formatted(gameId));
        }

        // Ensure the User making the request is one of the players
        if (!username.equals(game.getBlackPlayerUsername()) && !username.equals(game.getWhitePlayerUsername())) {
            throw new UnauthorizedException();
        }

        return getValidMoves(game, startingSquare, playerColor);

    }

    /**
     * Get all valid moves based on the given Game, and optional starting square and
     * player color
     * 
     * @param game           the Game for which to get valid moves
     * @param startingSquare the optional starting square to consider
     * @param playerColor    the optional player color to consider
     * @return
     */
    public List<String> getValidMoves(Game game, Optional<int[]> startingSquare,
            Optional<String> playerColor) {

        // Get the board from the game
        String FEN = game.getFEN();

        String[] moves = game.getMoves().split(" ");

        return getValidMoves(FEN, Optional.ofNullable(moves), startingSquare, playerColor);

    }

    /**
     * Get all valid moves based on the given FEN, and optional starting square and
     * player color. Note that since FEN doesn't include previous moves, castling is
     * left out of the result set.
     * 
     * @param fen            the FEN notation for the current game state
     * @param startingSquare the optional start square from which to get valid moves
     * @param playerColor    the optional player color for who to get valid moves
     * @return
     */
    public List<String> getValidMoves(String fen, Optional<String[]> moves, Optional<int[]> startingSquare,
            Optional<String> playerColor) {

        String[][] grid = FENToGrid(fen);

        return getValidMoves(grid, moves, startingSquare, playerColor, false, true);

    }

    /**
     * Get all valid moves based on the given 2-D String array, and optional
     * starting square and player color. Note that these results won't contain
     * castling, since its validity is unknown
     * 
     * @param grid           the grid of squares to use to determine valid moves -
     *                       FEN converted to a 2D array
     * @param startingSquare the starting square from which to find valid moves
     * @param playerColor    the player color for whom to find valid moves
     * @return
     */
    public List<String> getValidMoves(String[][] grid, Optional<String[]> previousMoves, Optional<int[]> startingSquare,
            Optional<String> playerColor, boolean ignoreCheck, boolean includeAnnotations) {

        // Create a list of all possible starting squares
        List<int[]> startingSquareList = new ArrayList<int[]>();

        // Add only the relevant starting squares
        if (startingSquare.isPresent()) {
            startingSquareList.add(startingSquare.get());
        } else {
            startingSquareList = getStartingSquaresFromGrid(grid, playerColor);
        }

        // List of all possible moves
        List<String> moves = new ArrayList<String>();

        // Add the moves from each starting position
        for (int[] start : startingSquareList) {
            List<String> pieceMoves = findValidPieceMoves(grid, previousMoves, start, ignoreCheck, includeAnnotations);
            moves.addAll(pieceMoves);
        }

        return moves;

    }

    public List<Game> getGameStates(String gameId) {

        Game game = findById(UUID.fromString(gameId));

        if (game == null) {
            throw new NotFoundException("Game", "gameId: " + gameId);
        }

        List<Game> gameStates = new ArrayList<>();

        Game currentState = new Game();

        gameStates.add(currentState);

        for (String move : game.getMoves().split(" ")) {

            doMoveOnGame(currentState, MoveDTO.fromString(move));

            currentState = new Game();

            currentState.setFEN(game.getFEN());
            currentState.setMoves(game.getMoves());

            gameStates.add(currentState);

        }

        return gameStates;
    }

    // #endregion

    // #region private helper

    // #region findValidMoves

    protected List<String> findValidPieceMoves(String[][] grid, Optional<String[]> previousMoves, int[] start,
            boolean ignoreCheck, boolean includeAnnotations) {
        int x = start[0], y = start[1];

        List<String> validMoves = new ArrayList<String>();

        switch (grid[y][x]) {
            case "R", "r":
                validMoves = findValidRookMoves(grid, start, ignoreCheck, includeAnnotations);
                break;
            case "N", "n":
                validMoves = findValidKnightMoves(grid, start, ignoreCheck, includeAnnotations);
                break;
            case "B", "b":
                validMoves = findValidBishopMoves(grid, start, ignoreCheck, includeAnnotations);
                break;
            case "K", "k":
                validMoves = findValidKingMoves(grid, previousMoves, start, ignoreCheck, includeAnnotations);
                break;
            case "Q", "q":
                validMoves = findValidQueenMoves(grid, start, ignoreCheck, includeAnnotations);
                break;
            case "P", "p":
                validMoves = findValidPawnMoves(grid, previousMoves, start, ignoreCheck, includeAnnotations);
                break;
        }

        return validMoves;
    }

    protected List<String> findValidRookMoves(String[][] grid, int[] start, boolean ignoreCheck,
            boolean includeAnnotations) {

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
            String opponentColor = grid[y][x].equals(grid[y][x].toLowerCase()) ? "w" : "b";

            while (0 <= x2 && x2 < grid[0].length && 0 <= y2 && y2 < grid.length) {
                // if this square is the same color as the rook, break while
                if (isSameColorPiece(grid, x, y, x2, y2)) {
                    break;
                }

                // update the pieces to resemble the attempted move
                gridAfterMove[y2][x2] = grid[y][x];
                gridAfterMove[y2 - dir[1]][x2 - dir[0]] = " ";

                // if moving to this square leaves the king checked, skip while iteration
                if (!ignoreCheck && isInCheck(gridAfterMove, playerColor)) {
                    if (!grid[y2][x2].equals(" ")) {
                        break;
                    } else {
                        x2 += dir[0];
                        y2 += dir[1];
                        continue;
                    }
                }

                MoveDTO move = new MoveDTO();

                move.setPiece(grid[y][x]);
                move.setStartSquare(new int[] { x, y });
                move.setDestSquare(new int[] { x2, y2 });
                if (includeAnnotations) {
                    move.setIsCapture(!grid[y2][x2].equals(" "));
                    move.setIsCheck(isInCheck(gridAfterMove, opponentColor));
                    boolean hasNoMoves = isInMate(gridAfterMove, opponentColor);
                    move.setIsMate(move.getIsCheck() && hasNoMoves);
                    move.setIsStalemate(!move.getIsCheck() && hasNoMoves);
                }

                movesList.add(move.toString());

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

    protected List<String> findValidKnightMoves(String[][] grid, int[] start, boolean ignoreCheck,
            boolean includeAnnotations) {

        int x = start[0], y = start[1];

        List<String> movesList = new ArrayList<String>();

        String[][] gridAfterMove = Arrays.stream(grid).map(row -> row.clone()).toArray(String[][]::new);

        String playerColor = grid[y][x].equals(grid[y][x].toLowerCase()) ? "b" : "w";
        String opponentColor = grid[y][x].equals(grid[y][x].toLowerCase()) ? "w" : "b";

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
                gridAfterMove[y][x] = grid[y][x];
                gridAfterMove[y2][x2] = grid[y2][x2];
                continue;
            }

            MoveDTO move = new MoveDTO();

            move.setPiece(grid[y][x]);
            move.setStartSquare(new int[] { x, y });
            move.setDestSquare(new int[] { x2, y2 });
            if (includeAnnotations) {
                move.setIsCapture(!grid[y2][x2].equals(" "));
                move.setIsCheck(isInCheck(gridAfterMove, opponentColor));
                boolean hasNoMoves = isInMate(gridAfterMove, opponentColor);
                move.setIsMate(move.getIsCheck() && hasNoMoves);
                move.setIsStalemate(!move.getIsCheck() && hasNoMoves);
            }

            movesList.add(move.toString());

            // return grid to regular state, ready for next move
            gridAfterMove[y][x] = grid[y][x];
            gridAfterMove[y2][x2] = grid[y2][x2];

        }

        return movesList;

    }

    protected List<String> findValidBishopMoves(String[][] grid, int[] start, boolean ignoreCheck,
            boolean includeAnnotations) {

        int x = start[0], y = start[1];

        List<String> movesList = new ArrayList<String>();

        String playerColor = grid[y][x].equals(grid[y][x].toLowerCase()) ? "b" : "w";
        String opponentColor = grid[y][x].equals(grid[y][x].toLowerCase()) ? "w" : "b";

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
                gridAfterMove[y2][x2] = grid[y][x];
                gridAfterMove[y2 - dir[1]][x2 - dir[0]] = " ";

                // if moving to this square leaves the king checked, skip while iteration
                if (!ignoreCheck && isInCheck(gridAfterMove, playerColor)) {
                    if (!grid[y2][x2].equals(" ")) {
                        break;
                    } else {
                        x2 += dir[0];
                        y2 += dir[1];
                        continue;
                    }
                }

                MoveDTO move = new MoveDTO();

                move.setPiece(grid[y][x]);
                move.setStartSquare(new int[] { x, y });
                move.setDestSquare(new int[] { x2, y2 });
                if (includeAnnotations) {
                    move.setIsCapture(!grid[y2][x2].equals(" "));
                    move.setIsCheck(isInCheck(gridAfterMove, opponentColor));
                    boolean hasNoMoves = isInMate(gridAfterMove, opponentColor);
                    move.setIsMate(move.getIsCheck() && hasNoMoves);
                    move.setIsStalemate(!move.getIsCheck() && hasNoMoves);
                }

                movesList.add(move.toString());

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

    protected List<String> findValidKingMoves(String[][] grid, Optional<String[]> previousMoves, int[] start,
            boolean ignoreCheck, boolean includeAnnotations) {

        int x = start[0], y = start[1];

        List<String> movesList = new ArrayList<String>();

        String[][] gridAfterMove = Arrays.stream(grid).map(row -> row.clone()).toArray(String[][]::new);

        String playerColor = grid[y][x].equals(grid[y][x].toLowerCase()) ? "b" : "w";
        String opponentColor = grid[y][x].equals(grid[y][x].toLowerCase()) ? "w" : "b";

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
                gridAfterMove[y][x] = grid[y][x];
                gridAfterMove[y2][x2] = grid[y2][x2];
                continue;
            }

            MoveDTO move = new MoveDTO();

            move.setPiece(grid[y][x]);
            move.setStartSquare(new int[] { x, y });
            move.setDestSquare(new int[] { x2, y2 });
            if (includeAnnotations) {
                move.setIsCapture(!grid[y2][x2].equals(" "));
                move.setIsCheck(isInCheck(gridAfterMove, opponentColor));
                boolean hasNoMoves = isInMate(gridAfterMove, opponentColor);
                move.setIsMate(move.getIsCheck() && hasNoMoves);
                move.setIsStalemate(!move.getIsCheck() && hasNoMoves);
            }

            movesList.add(move.toString());

            // return grid to regular state, ready for next move
            gridAfterMove[y][x] = grid[y][x];
            gridAfterMove[y2][x2] = grid[y2][x2];

        }

        if (previousMoves.isPresent() && x == 4) {

            boolean leftPossible = true, rightPossible = true;
            String leftRook, rightRook;
            int xl, xr, y2, increment;

            // Get the rooks names and coordinates based on playerColor
            if (playerColor.equals("w")) {
                leftRook = "Ra1";
                rightRook = "Ra8";
                xl = 0;
                xr = 7;
                y2 = 7;
                increment = 1;
            } else {
                leftRook = "rh8";
                rightRook = "rh1";
                xl = 7;
                xr = 0;
                y2 = 0;
                increment = -1;
            }

            // Check if the king, or either rook has been used in a previous move
            for (String move : previousMoves.get()) {
                if (move.contains(grid[y][x])) {
                    leftPossible = false;
                    rightPossible = false;
                    break;
                }
                if (move.contains(leftRook)) {
                    leftPossible = false;
                }
                if (move.contains(rightRook)) {
                    rightPossible = false;
                }
            }

            // check that all the spaces between the king and left rook are empty,
            // and that no square in the kings path would be check
            for (int x2 = x - increment; x2 != xl; x2 -= increment) {
                if (!grid[y2][x2].equals(" ")) {
                    leftPossible = false;
                    break;
                }
                if (Math.abs(x2 - x) <= 2) {
                    gridAfterMove[y2][x2] = grid[y][x];
                    gridAfterMove[y][x] = " ";
                    if (!ignoreCheck && isInCheck(gridAfterMove, playerColor)) {
                        leftPossible = false;
                    }
                    gridAfterMove[y2][x2] = grid[y2][x2];
                    gridAfterMove[y][x] = grid[y][x];
                }
            }

            if (leftPossible) {
                movesList.add(playerColor.equals("w") ? "Ke1c1" : "ke8g8");
            }

            // check that all the spaces between the king and right rook are empty
            for (int x2 = x + increment; x2 != xr; x2 += increment) {
                if (!grid[y2][x2].equals(" ")) {
                    rightPossible = false;
                    break;
                }
            }

            if (rightPossible) {
                movesList.add(playerColor.equals("w") ? "Ke1g1" : "ke8c8");
            }

        }

        return movesList;

    }

    protected List<String> findValidQueenMoves(String[][] grid, int[] start, boolean ignoreCheck,
            boolean includeAnnotations) {
        List<String> result = new ArrayList<String>();
        result.addAll(findValidRookMoves(grid, start, ignoreCheck, includeAnnotations));
        result.addAll(findValidBishopMoves(grid, start, ignoreCheck, includeAnnotations));
        return result;
    }

    protected List<String> findValidPawnMoves(String[][] grid, Optional<String[]> previousMoves, int[] start,
            boolean ignoreCheck, boolean includeAnnotations) {

        int x = start[0], y = start[1];

        List<String> movesList = new ArrayList<String>();

        String[][] gridAfterMove = Arrays.stream(grid).map(row -> row.clone()).toArray(String[][]::new);

        String playerColor = grid[y][x].equals(grid[y][x].toLowerCase()) ? "b" : "w";
        String opponentColor = grid[y][x].equals(grid[y][x].toLowerCase()) ? "w" : "b";

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
            if (dir[0] != 0 && grid[y2][x2].trim().equals("")) {
                continue;
            }

            // If the move is not diagonal but the dest is not empty
            if (dir[0] == 0 && !grid[y2][x2].trim().equals("")) {
                continue;
            }

            // If the move is forward 2, but the space it's skipping is not empty
            if (Math.abs(dir[1]) > 1 && !grid[y + increment[1]][x2].trim().equals("")) {
                continue;
            }

            // If the move is forward 2, but the pawn isn't on its starting rank
            if (Math.abs(dir[1]) > 1 && !(y - dir[1] < 0 || y - dir[1] >= grid.length)) {
                continue;
            }

            gridAfterMove[y2][x2] = grid[y][x];
            gridAfterMove[y][x] = " ";

            // If the move leaves the king in check
            if (!ignoreCheck && isInCheck(gridAfterMove, playerColor)) {
                gridAfterMove[y][x] = grid[y][x];
                gridAfterMove[y2][x2] = grid[y2][x2];
                continue;
            }

            MoveDTO move = new MoveDTO();

            move.setPiece(grid[y][x]);
            move.setStartSquare(new int[] { x, y });
            move.setDestSquare(new int[] { x2, y2 });
            if (includeAnnotations) {
                move.setIsCapture(!grid[y2][x2].equals(" "));
                move.setIsCheck(isInCheck(gridAfterMove, opponentColor));
                boolean hasNoMoves = isInMate(gridAfterMove, opponentColor);
                move.setIsMate(move.getIsCheck() && hasNoMoves);
                move.setIsStalemate(!move.getIsCheck() && hasNoMoves);
            }

            movesList.add(move.toString());

            gridAfterMove[y][x] = grid[y][x];
            gridAfterMove[y2][x2] = grid[y2][x2];

        }

        // Check for en passant
        if (previousMoves.isPresent()) {

            String[] previousMovesList = previousMoves.get();

            String previousMove = previousMovesList[previousMovesList.length - 1];

            // if the last move was not a pawn move
            if (!previousMove.toLowerCase().contains("p")) {
                return movesList;
            }

            // if the last move was a capture
            if (previousMove.contains("x")) {
                return movesList;
            }

            int prevRank1, prevRank2, prevX, prevY;

            prevRank1 = Integer.parseInt(previousMove.substring(2, 3));
            prevRank2 = Integer.parseInt(previousMove.substring(4, 5));

            prevY = Math.abs(prevRank2 - 8);
            prevX = previousMove.charAt(1) - 'a';

            // if the current pawns rank is not the same as the last moves rank
            if (Math.abs(y - 8) != prevRank2) {
                return movesList;
            }

            // if the current pawn is not offset by 1 file from the last pawn
            if (Math.abs(x - prevX) != 1) {
                return movesList;
            }

            if (playerColor.equals("w") && (prevRank1 != 7 || prevRank1 - prevRank2 != 2)) {

                // if the last move was not from rank 7 to rank 5
                return movesList;

            } else if (playerColor.equals("b") && (prevRank1 != 2 || prevRank1 - prevRank2 != -2)) {

                // if the last move was not from rank 1 to rank 3
                return movesList;

            }

            // update the gridAfterMove
            gridAfterMove[y][x] = " ";
            gridAfterMove[prevY][prevX] = " ";
            gridAfterMove[y + increment[1]][prevX] = grid[y][x];

            // if ignoreCheck or the move doesn't leave the user in check, it is valid
            if (ignoreCheck || !isInCheck(gridAfterMove, playerColor)) {

                MoveDTO move = new MoveDTO();

                move.setPiece(grid[y][x]);
                move.setStartSquare(new int[] { x, y });
                move.setDestSquare(new int[] { prevX, y + increment[1] });

                if (includeAnnotations) {
                    move.setIsCapture(true);
                    move.setIsCheck(isInCheck(gridAfterMove, opponentColor));
                    boolean hasNoMoves = isInMate(gridAfterMove, opponentColor);
                    move.setIsMate(move.getIsCheck() && hasNoMoves);
                    move.setIsStalemate(!move.getIsCheck() && hasNoMoves);
                }

                movesList.add(move.toString());

            }

            // reset the gridAfterMove
            gridAfterMove[y][x] = grid[y][x];
            gridAfterMove[prevY][prevX] = grid[prevY][prevX];
            gridAfterMove[y + increment[1]][prevX] = grid[y + increment[1]][prevX];

        }

        return movesList;

    }

    // #endregion

    protected boolean isInCheck(String[][] grid, String playerColor) {

        List<String> opponentValidMoves = getValidMoves(grid, Optional.ofNullable(null), Optional.ofNullable(null),
                Optional.ofNullable(playerColor.equals("w") ? "b" : "w"), true, false);

        String kingLocation = "";

        for (int i = 0; i < grid.length; i++) {
            for (int j = 0; j < grid[0].length; j++) {
                if ((playerColor.equals("w") && grid[i][j].equals("K")) ||
                        (playerColor.equals("b") && grid[i][j].equals("k"))) {
                    kingLocation = "" + (char) (j + 'a') + Math.abs(i - 8);
                }
            }
        }

        for (String move : opponentValidMoves) {
            if (move.substring(3, 5).equals(kingLocation)) {
                return true;
            }
        }
        return false;
    }

    protected boolean isInMate(String[][] grid, String playerColor) {

        return getValidMoves(grid, Optional.ofNullable(null), Optional.ofNullable(null),
                Optional.ofNullable(playerColor), false, false).size() == 0;

    }

    protected List<int[]> findPlayerPieces(String[][] grid, String playerColor) {

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

    protected String[][] FENToGrid(String FEN) {
        // in order to allow for bigger board sizes this needs to be revised
        for (int index = 1; index <= 9; index++) {
            FEN = FEN.replace(Integer.toString(index), " ".repeat(index));
        }
        return Arrays.stream(FEN.split("/")).map(row -> row.split("")).toArray(String[][]::new);
    }

    protected String gridToFEN(String[][] grid) {
        String result = String.join("/", Arrays.stream(grid).map(row -> String.join("", row)).toArray(String[]::new));
        for (int i = 8; i >= 1; i--) {
            result = result.replace(" ".repeat(i), "" + i);
        }
        return result;
    }

    protected boolean isSameColorPiece(String[][] grid, int x1, int y1, int x2, int y2) {
        return (("RNBKQP".contains(grid[y1][x1]) && "RNBKQP".contains(grid[y2][x2]))
                || ("rnbkqp".contains(grid[y1][x1]) && "rnbkqp".contains(grid[y2][x2])));
    }

    protected List<int[]> getStartingSquaresFromGrid(String[][] grid, Optional<String> playerColor) {

        List<int[]> startingSquareList = new ArrayList<int[]>();

        // Add only the relevant starting squares
        if (playerColor.isPresent()) {
            startingSquareList.addAll(findPlayerPieces(grid, playerColor.get()));
        } else {
            startingSquareList.addAll(findPlayerPieces(grid, "w"));
            startingSquareList.addAll(findPlayerPieces(grid, "b"));
        }

        return startingSquareList;

    }

    // #endregion

}
