package com.jacob.backend.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.jacob.backend.data.Model.Game;
import com.jacob.backend.data.Model.User;
import com.jacob.backend.repository.interfaces.GameRepositoryInterface;
import com.jacob.backend.responses.exceptions.NotFoundException;

import java.util.ArrayList;
import java.util.List;
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
        assertThrows(NotFoundException.class, () -> {
            service.findAllByUsername("username");
        });

        // ASSERT

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

        // ASSERT

    }

    @Test
    public void create_whenInvokedByUnauthorizedPlayer_throwsException() {

        // MOCK

        // ACT

        // ASSERT

    }

    @Test
    public void cretae_whenInvokedWithUnregisteredPlayer_throwsException() {

        // MOCK

        // ACT

        // ASSERT

    }

    @Test
    public void update_whenInvokedWithValidArgs_updatesGame() {

        // MOCK

        // ACT

        // ASSERT

    }

    @Test
    public void update_whenInvokedByUnauthorizedUser_throwsException() {

        // MOCK

        // ACT

        // ASSERT

    }

    @Test
    public void doMove_whenInvokedWithValidArgs_updatesGame() {

        // MOCK

        // ACT

        // ASSERT

    }

    // TODO add more tests for doMove and below

    // #endregion

}
