package com.jacob.backend.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.jacob.backend.data.DTO.MoveDTO;
import com.jacob.backend.data.Model.Game;
import com.jacob.backend.data.Model.User;
import com.jacob.backend.repository.interfaces.GameRepositoryInterface;
import com.jacob.backend.responses.exceptions.MissingFieldException;
import com.jacob.backend.responses.exceptions.NotFoundException;
import com.jacob.backend.responses.exceptions.UnauthorizedException;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Tag("UnitTest")
@ExtendWith(MockitoExtension.class)
public class GameServiceTest {

    @Mock
    private GameRepositoryInterface mockGameRepo;

    @Mock
    private UserService mockUserService;

    @Mock
    private SessionService mockSessionService;

    @InjectMocks
    private GameService service;

    // #region CRUD

    @Test
    public void findById_whenInvokedWithValidArgs_getsGameById() {

        // MOCK
        when(mockGameRepo.getById(any(UUID.class))).thenReturn(new Game());

        // ACT
        Game game = service.findById(UUID.randomUUID());

        // ASSERT
        verify(mockGameRepo, times(1)).getById(any(UUID.class));
        assertNotNull(game);

    }

    @Test
    public void findAllByUserId_whenInvokedWithValidArgs_getsAllGamesByUserId() {

        // MOCK
        when(mockGameRepo.getAllByUserId(any(UUID.class))).thenReturn(new ArrayList<Game>());

        // ACT
        List<Game> games = service.findAllByUserId(UUID.randomUUID());

        // ASSERT
        verify(mockGameRepo, times(1)).getAllByUserId(any(UUID.class));
        assertEquals(0, games.size());

    }

    @Test
    public void findAllByUsername_whenInvokedWithValidArgs_getsAllGamesByUsername() {

        // MOCK
        User user = new User();
        user.setId(UUID.randomUUID());

        when(mockUserService.findByUsername(anyString())).thenReturn(user);
        when(mockGameRepo.getAllByUserId(any(UUID.class))).thenReturn(new ArrayList<Game>());

        // ACT
        List<Game> games = service.findAllByUsername("username");

        // ASSERT
        verify(mockUserService, times(1)).findByUsername("username");
        verify(mockGameRepo, times(1)).getAllByUserId(any(UUID.class));
        assertEquals(0, games.size());

    }

    @Test
    public void findAllByUsername_whenInvokedWithUnregisteredUsername_throwsException() {

        // MOCK
        when(mockUserService.findByUsername(anyString())).thenReturn(null);

        // ACT
        
        // ASSERT
        assertThrows(NotFoundException.class, () -> {
            service.findAllByUsername("username");
        });

    }

    @Test
    public void create_whenInvokedWithValidArgs_createsGame() {

        // MOCK
        UUID id = UUID.randomUUID();
        when(mockUserService.findByUsername(anyString())).thenReturn(new User());
        doAnswer((i) -> {
            ((Game) i.getArgument(0)).setId(id);
            return null;
        }).when(mockGameRepo).save(any(Game.class));

        // ACT
        String username = "whiteplayer";
        Game game = new Game();
        game.setWhitePlayerUsername(username);
        game.setBlackPlayerUsername("blackplayer");

        String gameId = service.create(username, game);

        // ASSERT
        verify(mockUserService, times(2)).findByUsername(anyString());
        verify(mockGameRepo, times(1)).save(any(Game.class));
        assertNotNull(gameId);

    }

    @Test
    public void create_whenInvokedwithGameMissingField_throwsException() {

        // MOCK

        // ACT
        String username = "username";
        Game game = new Game();

        // ASSERT
        MissingFieldException e = assertThrows(MissingFieldException.class, () -> {
            service.create(username, game);
        });
        assertTrue(e.getMessage().contains("missing field White Player Username"));

    }

    @Test
    public void create_whenInvokedByUnauthorizedPlayer_throwsException() {

        // MOCK

        // ACT
        String username = "username";
        Game game = new Game();
        game.setBlackPlayerUsername("blackplayer");
        game.setWhitePlayerUsername("whiteplayer");

        // ASSERT
        UnauthorizedException e = assertThrows(UnauthorizedException.class, () -> {
            service.create(username, game);
        });
        assertTrue(e.getMessage().contains("UNAUTHORIZED"));

    }

    @Test
    public void create_whenInvokedWithUnregisteredPlayer_throwsException() {

        // MOCK
        when(mockUserService.findByUsername(anyString())).thenReturn(null);

        // ACT
        assertThrows(NotFoundException.class, () -> {
            Game game = new Game();
            game.setBlackPlayerUsername("BlackPlayer");
            game.setWhitePlayerUsername("WhitePlayer");
            service.create("WhitePlayer", game);
        });

        // ASSERT
        verify(mockGameRepo, times(0)).save(any(Game.class));

    }

    @Test
    public void update_whenInvokedWithValidArgs_updatesGame() {

        // MOCK
        doNothing().when(mockGameRepo).update(any(Game.class));

        // ACT
        Game game = new Game();
        game.setBlackPlayerUsername("blackPlayer");
        game.setWhitePlayerUsername("whitePlayer");

        service.update("blackPlayer", game);

        // ASSERT
        verify(mockGameRepo, times(1)).update(game);

    }

    @Test
    public void update_whenInvokedByUnauthorizedUser_throwsException() {

        // MOCK

        // ACT
        Game game = new Game();
        game.setBlackPlayerUsername("blackPlayer");
        game.setWhitePlayerUsername("whitePlayer");

        assertThrows(UnauthorizedException.class, () -> {
            service.update("user", game);
        });

        // ASSERT
        verify(mockGameRepo, times(0)).update(any(Game.class));

    }

    @Test
    public void doMove_whenInvokedWithInvalidGameUUID_throwsException() {

        // MOCK
        when(mockSessionService.isValidUUID(anyString())).thenReturn(false);

        // ACT
        NotFoundException e = assertThrows(NotFoundException.class, () -> {
            service.doMove("username", "invalidid", new MoveDTO());
        });

        // ASSERT
        verify(mockSessionService, times(1)).isValidUUID(anyString());
        verify(mockGameRepo, times(0)).getById(any(UUID.class));
        verify(mockGameRepo, times(0)).update(any(Game.class));
        assertTrue(e.getMessage().contains("Game with ID: invalidid not found in database"));

    }

    @Test
    public void doMove_whenInvokedWithGameIdNotCorrespondingToGame_throwsException() {

        // MOCK
        when(mockSessionService.isValidUUID(anyString())).thenReturn(true);
        when(mockGameRepo.getById(any(UUID.class))).thenReturn(null);

        // ACT
        String id = UUID.randomUUID().toString();

        NotFoundException e = assertThrows(NotFoundException.class, () -> {
            service.doMove("username", id, new MoveDTO());
        });

        // ASSERT
        assertTrue(e.getMessage().contains("Game with ID: " + id + " not found in database"));

    }

    @Test
    public void doMove_whenInvokedByAUserWhoIsntAPlayer_throwsException() {

        // MOCK
        when(mockSessionService.isValidUUID(anyString())).thenReturn(true);
        when(mockGameRepo.getById(any(UUID.class))).thenReturn(new Game());

        // ACT
        assertThrows(UnauthorizedException.class, () -> {
            service.doMove("username", UUID.randomUUID().toString(), new MoveDTO());
        });

    }

    // #endregion

    @Test
    public void getValidMoves_whenInvokedWithInvalidGameId_throwsException() {

        // MOCK
        when(mockSessionService.isValidUUID(anyString())).thenReturn(false);

        // ACT
        NotFoundException e = assertThrows(NotFoundException.class, () -> {
            service.getValidMoves("username", "gameid", Optional.ofNullable(null), Optional.ofNullable(null));
        });

        // ASSERT
        assertTrue(e.getMessage().contains("Game with ID: gameid not found in database"));

    }

    @Test
    public void getValidMoves_whenInvokedWithNonexistentGameId_throwsException() {

        // MOCK
        when(mockSessionService.isValidUUID(anyString())).thenReturn(true);
        when(mockGameRepo.getById(any(UUID.class))).thenReturn(null);

        // ACT
        String id = UUID.randomUUID().toString();
        NotFoundException e = assertThrows(NotFoundException.class, () -> {
            service.getValidMoves("username", id, Optional.ofNullable(null), Optional.ofNullable(null));
        });

        // ASSERT
        assertTrue(e.getMessage().contains("Game with ID: " + id + " not found in database"));
        verify(mockGameRepo, times(1)).getById(any(UUID.class));

    }

    @Test
    public void getValidMoves_whenInvokedByUnauthorizedUser_throwsException() {

        // MOCK
        when(mockSessionService.isValidUUID(anyString())).thenReturn(true);
        when(mockGameRepo.getById(any(UUID.class))).thenReturn(new Game());

        // ASSERT
        UnauthorizedException e = assertThrows(UnauthorizedException.class, () -> {
            service.getValidMoves("username", UUID.randomUUID().toString(), Optional.ofNullable(null),
                    Optional.ofNullable(null));
        });

        // ACT
        assertTrue(e.getMessage().contains("UNAUTHORIZED"));
        verify(mockSessionService, times(1)).isValidUUID(anyString());
        verify(mockGameRepo, times(1)).getById(any(UUID.class));

    }

    @Nested
    class DefaultStartingPosition {

        Game game;

        @BeforeEach
        public void createGame() {
            game = new Game();
            game.setBlackPlayerUsername("blackPlayer");
            game.setWhitePlayerUsername("whitePlayer");
            game.setFEN("rnbqkbnr/pppppppp/8/8/8/8/PPPPPPPP/RNBQKBNR");
            game.setMoves("");
            game.setMoveTimes("");

            // MOCK
            when(mockSessionService.isValidUUID(anyString())).thenReturn(true);
            when(mockGameRepo.getById(any(UUID.class))).thenReturn(game);

        }

        @Test
        public void doMove_whenInvokedWithValidArgs_updatesGame() {

            // MOCK
            doNothing().when(mockGameRepo).update(any(Game.class));

            // ACT
            MoveDTO move = new MoveDTO();
            move.setPiece("P");
            move.setStartSquare(new int[] { 0, 6 });
            move.setDestSquare(new int[] { 0, 4 });
            move.setMiliseconds(100);
            String id = UUID.randomUUID().toString();

            service.doMove("whitePlayer", id, move);

            // ASSERT
            verify(mockSessionService, times(1)).isValidUUID(id);
            verify(mockGameRepo, times(1)).getById(UUID.fromString(id));
            verify(mockGameRepo, times(1)).update(game);
            assertEquals("rnbqkbnr/pppppppp/8/8/P7/8/1PPPPPPP/RNBQKBNR", game.getFEN());
            assertEquals("1. Pa2a4", game.getMoves());
            assertEquals("100", game.getMoveTimes());

        }

        @Test
        public void doMove_whenInvokedWithAnInvalidMove_throwsException() {

            // MOCK

            // ASSERT
            MoveDTO move = new MoveDTO();
            move.setDestSquare(new int[] { 4, 5 });
            move.setStartSquare(new int[] { 4, 5 });

            RuntimeException e = assertThrows(RuntimeException.class, () -> {
                service.doMove("blackPlayer", UUID.randomUUID().toString(), move);
            });

            // ASSERT
            assertTrue(e.getMessage().contains("Attempting to perform an Invalid Move"));

        }

        @Test
        public void getValidMoves_whenInvokedWithValidArgs_returnsValidMoves() {

            // MOCK

            // ACT
            List<String> validMoves = service.getValidMoves("blackPlayer", UUID.randomUUID().toString(),
                    Optional.ofNullable(null), Optional.ofNullable(null));

            // ASSERT
            assertEquals(40, validMoves.size());
            assertEquals(16, validMoves.stream().filter((s) -> {
                return s.startsWith("p");
            }).count());
            assertEquals(4, validMoves.stream().filter((s) -> {
                return s.startsWith("n");
            }).count());
            assertEquals(12, validMoves.stream().filter((s) -> {
                return s.endsWith("6");
            }).count());
            assertEquals(8, validMoves.stream().filter((s) -> {
                return s.endsWith("5");
            }).count());
            assertEquals(16, validMoves.stream().filter((s) -> {
                return s.startsWith("P");
            }).count());
            assertEquals(4, validMoves.stream().filter((s) -> {
                return s.startsWith("N");
            }).count());
            assertEquals(12, validMoves.stream().filter((s) -> {
                return s.endsWith("3");
            }).count());
            assertEquals(8, validMoves.stream().filter((s) -> {
                return s.endsWith("4");
            }).count());

        }

        @Test
        public void getValidMoves_whenInvokedWithPlayerColor_returnsValidMoves() {

            // MOCK

            // ACT
            List<String> validMoves = service.getValidMoves("blackPlayer", UUID.randomUUID().toString(),
                    Optional.ofNullable(null), Optional.ofNullable("b"));

            // ASSERT
            assertEquals(20, validMoves.size());
            assertEquals(16, validMoves.stream().filter((s) -> {
                return s.startsWith("p");
            }).count());
            assertEquals(4, validMoves.stream().filter((s) -> {
                return s.startsWith("n");
            }).count());
            assertEquals(12, validMoves.stream().filter((s) -> {
                return s.endsWith("6");
            }).count());
            assertEquals(8, validMoves.stream().filter((s) -> {
                return s.endsWith("5");
            }).count());

        }

        @Test
        public void getValidMoves_whenInvokedWithStartingSquare_returnsValidMoves() {

            // MOCK

            // ACT
            List<String> validMoves = service.getValidMoves("blackPlayer", UUID.randomUUID().toString(),
                    Optional.ofNullable(new int[] { 1, 0 }), Optional.ofNullable(null));

            // ASSERT
            assertEquals(2, validMoves.size());
            assertTrue(validMoves.contains("nb8a6"));
            assertTrue(validMoves.contains("nb8c6"));

        }

        @Test
        public void getValidMoves_whenInvokedWithStartingSquareAndPlayerColor_returnsValidMoves() {

            // MOCK

            // ACT
            List<String> validMoves = service.getValidMoves("blackPlayer", UUID.randomUUID().toString(),
                    Optional.ofNullable(new int[] { 1, 0 }), Optional.ofNullable("w"));

            // ASSERT
            assertEquals(2, validMoves.size());

        }

    }

    @Nested
    class RandomStartingPosition1 {

        Game game;

        @BeforeEach
        public void setupGame() {
            game = new Game();
            game.setBlackPlayerUsername("Bronstein");
            game.setWhitePlayerUsername("Kholmov");
            game.setFEN("r1b2r1k/4qp1p/p2ppb1Q/4nP2/1p1NP3/2N5/PPP4P/2KR1BR1");
            game.setMoves(
                    "1. e4 c5 2. Nf3 Nf6 3. Nc3 d6 4. d4 cxd4 5. Nxd4 a6 " +
                            "6. Bg5 e6 7. f4 Be7 8. Qf3 Qc7 9. O-O-O Nbd7 10. g4 b5 " +
                            "11. Bxf6 gxf6 12. f5 Ne5 13. Qh3 O-O 14. g5 b4 15. gxf6 Bxf6 " +
                            "16. Rg1+ Kh8 17. Qh6 Qe7");
            game.setMoveTimes("");
        }

        @Test
        public void doMove_whenInvokedWithValidArgs_updatesGame() {

            // MOCK
            when(mockSessionService.isValidUUID(anyString())).thenReturn(true);
            when(mockGameRepo.getById(any(UUID.class))).thenReturn(game);
            doNothing().when(mockGameRepo).update(any(Game.class));

            // ACT
            MoveDTO move = new MoveDTO();
            move.setPiece("N");
            move.setStartSquare(new int[] { 3, 4 });
            move.setDestSquare(new int[] { 2, 2 });
            move.setMiliseconds(100);
            String id = UUID.randomUUID().toString();

            service.doMove("Kholmov", id, move);

            // ASSERT
            verify(mockSessionService, times(1)).isValidUUID(id);
            verify(mockGameRepo, times(1)).getById(UUID.fromString(id));
            verify(mockGameRepo, times(1)).update(game);
            assertEquals("r1b2r1k/4qp1p/p1Nppb1Q/4nP2/1p2P3/2N5/PPP4P/2KR1BR1", game.getFEN());
            assertEquals("1. e4 c5 2. Nf3 Nf6 3. Nc3 d6 4. d4 cxd4 5. Nxd4 a6 " + //
                    "6. Bg5 e6 7. f4 Be7 8. Qf3 Qc7 9. O-O-O Nbd7 10. g4 b5 " + //
                    "11. Bxf6 gxf6 12. f5 Ne5 13. Qh3 O-O 14. g5 b4 15. gxf6 Bxf6 " + //
                    "16. Rg1+ Kh8 17. Qh6 Qe7 18. Nd4c6", game.getMoves());
            assertEquals("100", game.getMoveTimes());

        }

        @Test
        public void getValidMoves_whenInvokedWithValidArgs_returnsValidMoves() {

            // MOCK
            when(mockSessionService.isValidUUID(anyString())).thenReturn(true);
            when(mockGameRepo.getById(any(UUID.class))).thenReturn(game);

            // ACT
            List<String> validMoves = service.getValidMoves("Kholmov", UUID.randomUUID().toString(),
                    Optional.ofNullable(null), Optional.ofNullable("w"));

            // ASSERT
            assertEquals(6, validMoves.stream().filter((s) -> {
                return s.startsWith("P");
            }).count());
            assertEquals(11, validMoves.stream().filter((s) -> {
                return s.startsWith("N");
            }).count());
            assertEquals(11, validMoves.stream().filter((s) -> {
                return s.startsWith("R");
            }).count());
            assertEquals(7, validMoves.stream().filter((s) -> {
                return s.startsWith("B");
            }).count());
            assertEquals(12, validMoves.stream().filter((s) -> {
                return s.startsWith("Q");
            }).count());
            assertEquals(2, validMoves.stream().filter((s) -> {
                return s.startsWith("K");
            }).count());
            assertEquals(49, validMoves.size());
            
        }

        @Test
        public void getValidMoves_whenInvokedWithoutIncludeAnnotations_returnsValidMoves() {

            // MOCK

            // ACT
            List<String> validMoves = service.getValidMoves(service.FENToGrid(game.getFEN()), Optional.ofNullable(null),
                    Optional.ofNullable("w"), false, false);

            // ASSERT
            assertEquals(49, validMoves.size());
            assertEquals(0, validMoves.stream().filter((s) -> {
                return s.contains("x") || s.contains("+") || s.contains("#");
            }).count());

        }

        @Test
        public void getValidMoves_whenInvokedWithIncludeAnnotations_returnsValidMoves() {

            // MOCK

            // ACT
            List<String> validMoves = service.getValidMoves(service.FENToGrid(game.getFEN()), Optional.ofNullable(null),
                    Optional.ofNullable("w"), false, true);

            // ASSERT
            assertEquals(49, validMoves.size());
            assertEquals(6, validMoves.stream().filter((s) -> {
                return s.contains("x");
            }).count());
            assertEquals(5, validMoves.stream().filter((s) -> {
                return s.contains("+");
            }).count());
            assertEquals(0, validMoves.stream().filter((s) -> {
                return s.contains("#");
            }).count());
            assertTrue(validMoves.contains("Nd4c6"));
            assertTrue(validMoves.contains("Qh6xf8+"));

        }

    }

    @Nested
    class RandomStartingPosition2 {

        Game game;

        @BeforeEach
        public void createGame() {
            game = new Game();
            game.setBlackPlayerUsername("Sanz");
            game.setWhitePlayerUsername("Estaban");
            game.setFEN("8/pR4pk/1b2p3/2p3p1/N1p5/7P/PP1r2P1/6K1");
            game.setMoves(
                    "1. e4 e6 2. d3 d5 3. Nc3 Nf6 4. e5 Nfd7 5. f4 Bb4 6. Bd2 O-O " +
                            "7. Nf3 f6 8. d4 c5 9. Nb5 fxe5 10. dxe5 Rxf4 11. c3 Re4+ 12. Be2 Ba5 " +
                            "13. O-O Nxe5 14. Nxe5 Rxe5 15. Bf4 Rf5 16. Bd3 Rf6 17. Qc2 h6 18. Be5 Nd7 " +
                            "19. Bxf6 Nxf6 20. Rxf6 Qxf6 21. Rf1 Qe7 22. Bh7+ Kh8 23. Qg6 Bd7 24. Rf7 Qg5 " +
                            "25. Qxg5 hxg5 26. Rxd7 Kxh7 27. Rxb7 Bb6 28. c4 dxc4 29. Nc3 Rd8 30. h3 Rd2 31. Na4");
            game.setMoveTimes("");
        }

        @Test
        public void doMove_whenInvokedWithValidArgs_updatesGame() {

            // MOCK
            when(mockSessionService.isValidUUID(anyString())).thenReturn(true);
            when(mockGameRepo.getById(any(UUID.class))).thenReturn(game);
            doNothing().when(mockGameRepo).update(any(Game.class));

            // ACT
            MoveDTO move = new MoveDTO();
            move.setPiece("r");
            move.setStartSquare(new int[] { 3, 6 });
            move.setDestSquare(new int[] { 1, 6 });
            move.setMiliseconds(100);
            move.setIsCapture(true);
            String id = UUID.randomUUID().toString();

            service.doMove("Sanz", id, move);

            // ASSERT
            verify(mockSessionService, times(1)).isValidUUID(anyString());
            verify(mockGameRepo, times(1)).getById(UUID.fromString(id));
            verify(mockGameRepo, times(1)).update(game);
            assertEquals("8/pR4pk/1b2p3/2p3p1/N1p5/7P/Pr4P1/6K1", game.getFEN());
            assertEquals("1. e4 e6 2. d3 d5 3. Nc3 Nf6 4. e5 Nfd7 5. f4 Bb4 6. Bd2 O-O " +
                            "7. Nf3 f6 8. d4 c5 9. Nb5 fxe5 10. dxe5 Rxf4 11. c3 Re4+ 12. Be2 Ba5 " +
                            "13. O-O Nxe5 14. Nxe5 Rxe5 15. Bf4 Rf5 16. Bd3 Rf6 17. Qc2 h6 18. Be5 Nd7 " +
                            "19. Bxf6 Nxf6 20. Rxf6 Qxf6 21. Rf1 Qe7 22. Bh7+ Kh8 23. Qg6 Bd7 24. Rf7 Qg5 " +
                            "25. Qxg5 hxg5 26. Rxd7 Kxh7 27. Rxb7 Bb6 28. c4 dxc4 29. Nc3 Rd8 30. h3 Rd2 31. Na4 rd2xb2", game.getMoves());

        }

        @Test
        public void getValidMoves_whenInvokedWithValidArgs_returnsValidMoves() {

            // MOCK
            when(mockSessionService.isValidUUID(anyString())).thenReturn(true);
            when(mockGameRepo.getById(any(UUID.class))).thenReturn(game);

            // ACT
            List<String> validMoves = service.getValidMoves("Sanz", UUID.randomUUID().toString(),
                    Optional.ofNullable(null), Optional.ofNullable("b"));

            // ASSERT
            assertEquals(5, validMoves.stream().filter((s) -> {
                return s.startsWith("p");
            }).count());
            assertEquals(0, validMoves.stream().filter((s) -> {
                return s.startsWith("n");
            }).count());
            assertEquals(12, validMoves.stream().filter((s) -> {
                return s.startsWith("r");
            }).count());
            assertEquals(3, validMoves.stream().filter((s) -> {
                return s.startsWith("b");
            }).count());
            assertEquals(0, validMoves.stream().filter((s) -> {
                return s.startsWith("q");
            }).count());
            assertEquals(4, validMoves.stream().filter((s) -> {
                return s.startsWith("k");
            }).count());
            assertEquals(24, validMoves.size());

        }

        @Test
        public void getValidMoves_whenInvokedWithoutIncludeAnnotations_returnsValidMoves() {

            // ACT
            List<String> validMoves = service.getValidMoves(service.FENToGrid(game.getFEN()), Optional.ofNullable(null),
                    Optional.ofNullable("b"), false, false);

            // ASSERT
            assertEquals(24, validMoves.size());
            assertEquals(0, validMoves.stream().filter((s) -> {
                return s.contains("x") || s.contains("+") || s.contains("#");
            }).count());

        }

        @Test
        public void getValidMoves_whenInvokedWithIncludeAnnotations_returnsValidMoves() {

            // MOCK

            // ACT
            List<String> validMoves = service.getValidMoves(service.FENToGrid(game.getFEN()), Optional.ofNullable(null),
                    Optional.ofNullable("b"), false, true);

            // ASSERT
            assertEquals(24, validMoves.size());
            assertEquals(2, validMoves.stream().filter((s) -> {
                return s.contains("x");
            }).count());
            assertEquals(2, validMoves.stream().filter((s) -> {
                return s.contains("+");
            }).count());
            assertEquals(0, validMoves.stream().filter((s) -> {
                return s.contains("#");
            }).count());

        }

    }

    @Nested
    class RandomStartingPosition3 {

        Game game;

        @BeforeEach
        public void createGame() {
            game = new Game();
            game.setWhitePlayerUsername("Lasker");
            game.setBlackPlayerUsername("Thomas");
            game.setFEN("rn3rk1/pbppq1pp/1p2pb2/4N2Q/3PN3/3B4/PPP2PPP/R3K2R");
            game.setMoves("1. d4 e6 2. Nf3 f5 3. Nc3 Nf6 4. Bg5 Be7 5. Bxf6 Bxf6 " +
                    "6. e4 fxe4 7. Nxe4 b6 8. Ne5 O-O 9. Bd3 Bb7 10. Qh5 Qe7");
            game.setMoveTimes("1000 3500 2000 5000 4750 10000 12565 8750 9435 3456");

            // MOCK
            when(mockSessionService.isValidUUID(anyString())).thenReturn(true);
            when(mockGameRepo.getById(any(UUID.class))).thenReturn(game);

        }

        @Test
        public void doMove_whenInvokedWithValidArgs_updatesGame() {

            // MOCK
            doNothing().when(mockGameRepo).update(any(Game.class));

            // ACT
            MoveDTO move = new MoveDTO();
            move.setPiece("Q");
            move.setStartSquare(new int[] { 7, 3 });
            move.setDestSquare(new int[] { 7, 1 });
            move.setMiliseconds(9999);
            move.setIsCapture(true);
            move.setIsCheck(true);
            String id = UUID.randomUUID().toString();

            service.doMove("Lasker", id, move);

            // ASSERT
            verify(mockSessionService, times(1)).isValidUUID(anyString());
            verify(mockGameRepo, times(1)).getById(UUID.fromString(id));
            verify(mockGameRepo, times(1)).update(game);
            assertEquals("rn3rk1/pbppq1pQ/1p2pb2/4N3/3PN3/3B4/PPP2PPP/R3K2R", game.getFEN());
            assertEquals("1. d4 e6 2. Nf3 f5 3. Nc3 Nf6 4. Bg5 Be7 5. Bxf6 Bxf6 " +
                    "6. e4 fxe4 7. Nxe4 b6 8. Ne5 O-O 9. Bd3 Bb7 10. Qh5 Qe7 11. Qh5xh7+", game.getMoves());

        }

        @Test
        public void getValidMoves_whenInvokedWithValidArgs_returnsValidMoves() {

            // MOCK

            // ACT
            List<String> validMoves = service.getValidMoves("Lasker", UUID.randomUUID().toString(),
                    Optional.ofNullable(null), Optional.ofNullable("w"));

            // ASSERT
            assertEquals(13, validMoves.stream().filter((s) -> {
                return s.startsWith("P");
            }).count());
            assertEquals(14, validMoves.stream().filter((s) -> {
                return s.startsWith("N");
            }).count());
            assertEquals(5, validMoves.stream().filter((s) -> {
                return s.startsWith("R");
            }).count());
            assertEquals(5, validMoves.stream().filter((s) -> {
                return s.startsWith("B");
            }).count());
            assertEquals(13, validMoves.stream().filter((s) -> {
                return s.startsWith("Q");
            }).count());
            assertEquals(4, validMoves.stream().filter((s) -> {
                return s.startsWith("K");
            }).count());
            assertEquals(54, validMoves.size());

        }

        @Test
        public void getValidMoves_whenInvokedWithValidArgs_returnsValidMovesWithAnnotations() {

            // MOCK

            // ACT
            List<String> validMoves = service.getValidMoves("Lasker", UUID.randomUUID().toString(),
                    Optional.ofNullable(null), Optional.ofNullable("w"));

            // ASSERT
            assertEquals(3, validMoves.stream().filter((s) -> {
                return s.contains("x");
            }).count());
            assertEquals(3, validMoves.stream().filter((s) -> {
                return s.contains("+");
            }).count());
            assertEquals(0, validMoves.stream().filter((s) -> {
                return s.contains("#");
            }).count());
            assertEquals(54, validMoves.size());

        }

        @Test
        public void doMove_whenCalledSequentially_updatesGame() {

            // MOCK
            doNothing().when(mockGameRepo).update(any(Game.class));

            // ACT
            String id = UUID.randomUUID().toString();

            MoveDTO move;

            move = new MoveDTO();
            move.setPiece("Q");
            move.setStartSquare(new int[] { 7, 3 });
            move.setDestSquare(new int[] { 7, 1 });
            move.setIsCapture(true);
            move.setIsCheck(true);

            service.doMove("Lasker", id, move);

            move = new MoveDTO();
            move.setPiece("k");
            move.setStartSquare(new int[] { 6, 0 });
            move.setDestSquare(new int[] { 7, 1 });
            move.setIsCapture(true);
            move.setIsCheck(false);

            service.doMove("Thomas", id, move);

            move = new MoveDTO();
            move.setPiece("N");
            move.setStartSquare(new int[] { 4, 4 });
            move.setDestSquare(new int[] { 5, 2 });
            move.setIsCapture(true);
            move.setIsCheck(true);

            service.doMove("Lasker", id, move);

            move = new MoveDTO();
            move.setPiece("k");
            move.setStartSquare(new int[] { 7, 1 });
            move.setDestSquare(new int[] { 7, 2 });
            move.setIsCapture(false);
            move.setIsCheck(false);

            service.doMove("Thomas", id, move);

            move = new MoveDTO();
            move.setPiece("N");
            move.setStartSquare(new int[] { 4, 3 });
            move.setDestSquare(new int[] { 6, 4 });
            move.setIsCapture(false);
            move.setIsCheck(true);

            service.doMove("Lasker", id, move);

            move = new MoveDTO();
            move.setPiece("k");
            move.setStartSquare(new int[] { 7, 2 });
            move.setDestSquare(new int[] { 6, 3 });
            move.setIsCapture(false);
            move.setIsCheck(false);

            service.doMove("Thomas", id, move);

            move = new MoveDTO();
            move.setPiece("P");
            move.setStartSquare(new int[] { 7, 6 });
            move.setDestSquare(new int[] { 7, 4 });
            move.setIsCapture(false);
            move.setIsCheck(true);

            service.doMove("Lasker", id, move);

            move = new MoveDTO();
            move.setPiece("k");
            move.setStartSquare(new int[] { 6, 3 });
            move.setDestSquare(new int[] { 5, 4 });
            move.setIsCapture(false);
            move.setIsCheck(false);

            service.doMove("Thomas", id, move);

            move = new MoveDTO();
            move.setPiece("P");
            move.setStartSquare(new int[] { 6, 6 });
            move.setDestSquare(new int[] { 6, 5 });
            move.setIsCapture(false);
            move.setIsCheck(true);

            service.doMove("Lasker", id, move);

            move = new MoveDTO();
            move.setPiece("k");
            move.setStartSquare(new int[] { 5, 4 });
            move.setDestSquare(new int[] { 5, 5 });
            move.setIsCapture(false);
            move.setIsCheck(false);

            service.doMove("Thomas", id, move);

            move = new MoveDTO();
            move.setPiece("B");
            move.setStartSquare(new int[] { 3, 5 });
            move.setDestSquare(new int[] { 4, 6 });
            move.setIsCapture(false);
            move.setIsCheck(true);

            service.doMove("Lasker", id, move);

            move = new MoveDTO();
            move.setPiece("k");
            move.setStartSquare(new int[] { 5, 5 });
            move.setDestSquare(new int[] { 6, 6 });
            move.setIsCapture(false);
            move.setIsCheck(false);

            service.doMove("Thomas", id, move);

            move = new MoveDTO();
            move.setPiece("R");
            move.setStartSquare(new int[] { 7, 7 });
            move.setDestSquare(new int[] { 7, 6 });
            move.setIsCapture(false);
            move.setIsCheck(true);

            service.doMove("Lasker", id, move);

            move = new MoveDTO();
            move.setPiece("k");
            move.setStartSquare(new int[] { 6, 6 });
            move.setDestSquare(new int[] { 6, 7 });
            move.setIsCapture(false);
            move.setIsCheck(false);

            service.doMove("Thomas", id, move);

            move = new MoveDTO();
            move.setPiece("K");
            move.setStartSquare(new int[] { 4, 7 });
            move.setDestSquare(new int[] { 3, 6 });
            move.setIsCapture(false);
            move.setIsCheck(true);
            move.setIsMate(true);

            service.doMove("Lasker", id, move);

            // ASSERT
            verify(mockSessionService, times(15)).isValidUUID(id);
            verify(mockGameRepo, times(15)).getById(UUID.fromString(id));
            verify(mockGameRepo, times(15)).update(game);

        }

    }

}
