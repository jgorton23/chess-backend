package com.jacob.backend.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.assertj.core.util.Arrays;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.jacob.backend.data.DTO.ProfileDTO;
import com.jacob.backend.data.Model.Friend;
import com.jacob.backend.data.Model.User;
import com.jacob.backend.repository.interfaces.FriendRepositoryInterface;
import com.jacob.backend.repository.interfaces.UserRepositoryInterface;

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
    public void getProfile_whenInvokedWithValidArgs_getsUsersUsernameAndEmailAndFriendCountByUsername() {

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

    // #endregion

}
