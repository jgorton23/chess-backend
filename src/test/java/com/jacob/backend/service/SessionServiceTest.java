package com.jacob.backend.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.UUID;

import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.jacob.backend.data.Model.Session;
import com.jacob.backend.repository.interfaces.SessionRepositoryInterface;

@Tag("UnitTest")
@ExtendWith(MockitoExtension.class)
public class SessionServiceTest {

    @Mock
    private SessionRepositoryInterface mockSessionRepo;

    @InjectMocks
    private SessionService service;
    // #region CRUD

    @Test
    public void findById_whenInvoked_getsSessionById() {
        
        when(mockSessionRepo.getById(any(UUID.class))).thenReturn(new Session());

        service.findById(UUID.randomUUID());

        verify(mockSessionRepo, times(1)).getById(any(UUID.class));

    }

    @Test
    public void create_whenInvoked_deletesCurrentSessionandSavesNewSession() {

        doNothing().when(mockSessionRepo).deleteByUsername(anyString());

        UUID uuid = UUID.randomUUID();

        doAnswer((i) -> {
            ((Session) i.getArgument(0)).setId(uuid);
            return null;
        }).when(mockSessionRepo).save(any(Session.class));

        String id = service.create("username");

        verify(mockSessionRepo, times(1)).deleteByUsername("username");

        verify(mockSessionRepo, times(1)).save(any(Session.class));

        assertEquals(id, uuid.toString());

    }

    // #endregion

    // #region Helper

    @Test
    public void isValidUUID_whenInvokedWithValidUUID_thenReturnsTrue() {
        assertTrue(false, "");
    }

    // #endregion
}
