package com.jacob.backend.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

import java.util.UUID;

import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.jacob.backend.repository.interfaces.GameRepositoryInterface;

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

    }

    @Test
    public void findAllByUserId_whenInvokedWithValidArgs_getsAllGamesByUserId() {

    }

    @Test
    public void findAllByUsername_whenInvokedWithValidArgs_getsAllGamesByUsername() {

    }

    @Test
    public void findAllByUsername_whenInvokedWithUnregisteredUsername_throwsException() {

    }

    @Test
    public void create_whenInvokedWithValidArgs_createsGame() {

    }

    @Test
    public void create_whenInvokedwithGameMissingField_throwsException() {

    }

    @Test
    public void create_whenInvokedByUnauthorizedPlayer_throwsException() {

    }

    @Test
    public void cretae_whenInvokedWithUnregisteredPlayer_throwsException() {

    }

    @Test
    public void update_whenInvokedWithValidArgs_updatesGame() {

    }

    @Test
    public void update_whenInvokedByUnauthorizedUser_throwsException() {

    }

    @Test
    public void doMove_whenInvokedWithValidArgs_updatesGame() {

    }

    // TODO add more tests for doMove and below

    // #endregion

}
