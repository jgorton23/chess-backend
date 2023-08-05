package com.jacob.backend.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.jacob.backend.data.DTO.CredentialsDTO;
import com.jacob.backend.data.DTO.ProfileDTO;
import com.jacob.backend.data.Model.Friend;
import com.jacob.backend.data.Model.User;
import com.jacob.backend.repository.interfaces.FriendRepositoryInterface;
import com.jacob.backend.repository.interfaces.UserRepositoryInterface;
import com.jacob.backend.responses.exceptions.AlreadyFoundException;

@Tag("UnitTest")
@ExtendWith(MockitoExtension.class)
public class UserServiceTest {

    @Mock
    private UserRepositoryInterface mockUserRepo;

    @Mock
    private FriendRepositoryInterface mockFriendRepo;

    @Mock
    private AuthService mockAuthService;

    @InjectMocks
    private UserService service;

    // #region CRUD

    @Test
    public void findById_whenInvokedWithValidArgs_getsUserById() {

        // MOCK
        when(mockUserRepo.getById(any(UUID.class))).thenReturn(new User());

        // ACT
        UUID id = UUID.randomUUID();

        User user = service.findById(id);

        // ASSERT
        verify(mockUserRepo, times(1)).getById(id);
        assertNotNull(user);

    }

    @Test
    public void findByUsername_whenInvokedWithValidArgs_getsUserByUsername() {

        // MOCK
        when(mockUserRepo.getByUsername(anyString())).thenReturn(new User());

        // ACT
        String username = "username";

        User user = service.findByUsername(username);

        // ASSERT
        verify(mockUserRepo, times(1)).getByUsername(username);
        assertNotNull(user);

    }

    @Test
    public void getProfile_whenInvokedWithValidArgs_getsUserDataByUsername() {

        // MOCK
        User user = new User();
        user.setEmail("email");
        user.setUsername("username");
        user.setId(UUID.randomUUID());

        when(mockUserRepo.getByUsername(anyString())).thenReturn(user);

        List<Friend> friends = new ArrayList<Friend>();
        friends.add(new Friend());
        friends.add(new Friend());

        when(mockFriendRepo.getById(any(UUID.class))).thenReturn(friends);

        // ACT
        ProfileDTO profile = service.getProfile("username");

        // ASSERT
        assertEquals(2, profile.getFriends());
        assertEquals("email", profile.getEmail());
        assertEquals("username", profile.getUsername());
        verify(mockUserRepo, times(1)).getByUsername("username");
        verify(mockFriendRepo, times(1)).getById(user.getId());

    }

    @Test
    public void update_whenInvokedWithValidArgs_getsUserByUsername() {

        // MOCK
        User user = new User();

        when(mockUserRepo.getByUsername(anyString())).thenReturn(user);
        doNothing().when(mockUserRepo).update(any(User.class));

        // ACT
        String username = "username";
        CredentialsDTO creds = new CredentialsDTO();

        service.update(username, creds);

        // ASSERT
        verify(mockUserRepo, times(1)).getByUsername(username);
        verify(mockUserRepo, times(0)).userExists("newusername");
        verify(mockAuthService, times(0)).getRandomString(anyInt());
        verify(mockAuthService, times(0)).getPasswordHash(anyString());
        verify(mockUserRepo, times(1)).update(user);

    }

    @Test
    public void update_whenInvokedWithNewUsername_whereUserDoesntExist_updatesUsername() {

        // MOCK
        User user = new User();

        when(mockUserRepo.getByUsername(anyString())).thenReturn(user);
        when(mockUserRepo.userExists(anyString())).thenReturn(false);
        doNothing().when(mockUserRepo).update(any(User.class));

        // ACT
        String username = "username";
        CredentialsDTO creds = new CredentialsDTO();
        creds.setUsername("newusername");

        service.update(username, creds);

        // ASSERT
        verify(mockUserRepo, times(1)).getByUsername(username);
        verify(mockUserRepo, times(1)).userExists("newusername");
        verify(mockAuthService, times(0)).getRandomString(anyInt());
        verify(mockAuthService, times(0)).getPasswordHash(anyString());
        verify(mockUserRepo, times(1)).update(user);

    }

    @Test
    public void update_whenInvokedWithNewUsername_wherUserExists_throwsException() {

        // MOCK
        User user = new User();

        when(mockUserRepo.getByUsername(anyString())).thenReturn(user);
        when(mockUserRepo.userExists(anyString())).thenReturn(true);

        // ACT
        String username = "username";
        CredentialsDTO creds = new CredentialsDTO();
        creds.setUsername("newusername");

        assertThrows(AlreadyFoundException.class, () -> {
            service.update(username, creds);
        });

        // ASSERT
        verify(mockUserRepo, times(1)).getByUsername(username);
        verify(mockUserRepo, times(1)).userExists("newusername");
        verify(mockAuthService, times(0)).getRandomString(anyInt());
        verify(mockAuthService, times(0)).getPasswordHash(anyString());
        verify(mockUserRepo, times(0)).update(user);

    }

    @Test
    public void update_whenInvokedWithNewPasswordAndConfirm_wherePasswordsMatch_updatesPassword() {

        // MOCK
        User user = new User();

        when(mockUserRepo.getByUsername(anyString())).thenReturn(user);
        when(mockAuthService.getRandomString(20)).thenReturn("12345678901234567890");
        when(mockAuthService.getPasswordHash(anyString())).thenReturn("passwordhash-sha256");
        doNothing().when(mockUserRepo).update(any(User.class));

        // ACT
        String username = "username";
        CredentialsDTO creds = new CredentialsDTO();
        creds.setPassword("pass");
        creds.setConfirm("pass");

        service.update(username, creds);

        // ASSERT
        verify(mockUserRepo, times(1)).getByUsername(username);
        verify(mockUserRepo, times(0)).userExists(anyString());
        verify(mockAuthService, times(1)).getRandomString(anyInt());
        verify(mockAuthService, times(1)).getPasswordHash("pass" + "12345678901234567890");
        verify(mockUserRepo, times(1)).update(user);

    }

    // #endregion

}
