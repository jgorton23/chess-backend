package com.jacob.backend.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Answers.RETURNS_DEEP_STUBS;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
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
    public void doMove_whenInvokedWithValidArgs_updatesGame() {

        // MOCK
        Game game = new Game();
        game.setBlackPlayerUsername("blackPlayer");
        game.setWhitePlayerUsername("whitePlayer");
        game.setFEN("rnbqkbnr/pppppppp/8/8/8/8/PPPPPPPP/RNBQKBNR");
        game.setMoves("");
        game.setMoveTimes("");

        when(mockSessionService.isValidUUID(anyString())).thenReturn(true);
        when(mockGameRepo.getById(any(UUID.class))).thenReturn(game);
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

    @Test
    public void doMove_whenInvokedWithAnInvalidMove_throwsException() {

        // MOVE
        Game game = new Game();
        game.setBlackPlayerUsername("blackPlayer");
        game.setWhitePlayerUsername("whitePlayer");
        game.setFEN("rnbqkbnr/pppppppp/8/8/8/8/PPPPPPPP/RNBQKBNR");
        game.setMoves("");
        game.setMoveTimes("");
        when(mockGameRepo.getById(any(UUID.class))).thenReturn(game);
        when(mockSessionService.isValidUUID(anyString())).thenReturn(true);

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
        Game game = new Game();
        game.setBlackPlayerUsername("blackPlayer");
        game.setWhitePlayerUsername("whitePlayer");
        when(mockSessionService.isValidUUID(anyString())).thenReturn(true);
        when(mockGameRepo.getById(any(UUID.class))).thenReturn(game);

        // ASSERT
        UnauthorizedException e = assertThrowsExactly(UnauthorizedException.class, () -> {
            service.getValidMoves("username", UUID.randomUUID().toString(), Optional.ofNullable(null),
                    Optional.ofNullable(null));
        });

        // ACT
        assertTrue(e.getMessage().contains("UNAUTHORIZED"));
        verify(mockSessionService, times(1)).isValidUUID(anyString());
        verify(mockGameRepo, times(1)).getById(any(UUID.class));

    }

    @Test
    public void getValidMoves_whenInvokedWithStartingPosition_returnsValidMoves() {

        // MOCK
        Game game = new Game();
        game.setBlackPlayerUsername("blackPlayer");
        game.setWhitePlayerUsername("whitePlayer");
        game.setFEN("rnbqkbnr/pppppppp/8/8/8/8/PPPPPPPP/RNBQKBNR");
        game.setMoves("");
        game.setMoveTimes("");

        when(mockSessionService.isValidUUID(anyString())).thenReturn(true);
        when(mockGameRepo.getById(any(UUID.class))).thenReturn(game);

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

    // #endregion

}
