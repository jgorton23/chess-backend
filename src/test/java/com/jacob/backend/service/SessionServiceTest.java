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

import com.jacob.backend.data.Model.Session;
import com.jacob.backend.repository.interfaces.SessionRepositoryInterface;

import java.util.UUID;

@Tag("UnitTest")
@ExtendWith(MockitoExtension.class)
public class SessionServiceTest {

    @Mock
    private SessionRepositoryInterface mockSessionRepo;

    @InjectMocks
    private SessionService service;

    // #region CRUD

    /**
     * ensure that {@link SessionService#findById(UUID) SessionService.findById} invokes getById with the same UUID
     */
    @Test
    public void findById_whenInvokedWithValidArgs_getsSessionById() {
        
        // MOCK
        when(mockSessionRepo.getById(any(UUID.class))).thenReturn(new Session());

        // ACT
        UUID id = UUID.randomUUID();

        Session s = service.findById(id);

        // ASSERT
        verify(mockSessionRepo, times(1)).getById(id);
        assertNotNull(s);

    }

    /**
     * ensure that {@link SessionService#create(Session) SessionService.create}
     * deletes any existing Session and creates a new Session for the given username
     */
    @Test
    public void create_whenInvokedWithValidArgs_deletesCurrentSessionAndSavesNewSession() {

        // MOCK
        doNothing().when(mockSessionRepo).deleteByUsername(anyString());

        UUID uuid = UUID.randomUUID();

        doAnswer((i) -> {
            ((Session) i.getArgument(0)).setId(uuid);
            return null;
        }).when(mockSessionRepo).save(any(Session.class));

        // ACT
        String id = service.create("username");

        // ASSERT
        verify(mockSessionRepo, times(1)).deleteByUsername("username");
        verify(mockSessionRepo, times(1)).save(any(Session.class));
        assertEquals(id, uuid.toString());

    }

    /**
     * ensure that {@link SessionService#deleteById(UUID) SessionService.deleteById}
     * invokes deleteById on the repo layer with the same UUID
     */
    @Test
    public void deleteById_whenInvokedWithValidArgs_deletesSessionById() {

        // MOCK
        doNothing().when(mockSessionRepo).deleteById(any(UUID.class));

        // ACT
        UUID id = UUID.randomUUID();

        service.deleteById(id);

        // ASSERT
        verify(mockSessionRepo, times(1)).deleteById(id);

    }

    /**
     * ensure that {@link SessionService#update(UUID, String) SessionService.update}
     * invokes getById on the repo layer, updates the username of the resulting
     * Session, and invokes update on the repo layer
     */
    @Test
    public void update_whenInvokedWithValidArgs_getsSessionByIdAndSetsUsernameAndUpdatesSession() {

        // MOCK
        Session s = new Session();

        when(mockSessionRepo.getById(any(UUID.class))).thenReturn(s);
        doNothing().when(mockSessionRepo).update(any(Session.class));

        // ACT
        UUID id = UUID.randomUUID();
        String username = "username";

        service.update(id, username);

        // ASSERT
        verify(mockSessionRepo, times(1)).getById(id);
        verify(mockSessionRepo, times(1)).update(s);

    }

    /**
     * ensure that {@link SessionService#getUsernameById(String)
     * SessionService.getUsernameById} invokes getById on the repo layer
     * and returns the username associated with the resulting session
     */
    @Test
    public void getUsernameById_whenInvokedWithValidArgs_getsSessionByIdAndReturnsUsername() {

        // MOCK
        Session session = new Session();
        session.setUsername("username");

        when(mockSessionRepo.getById(any(UUID.class))).thenReturn(session);

        // ACT
        UUID id = UUID.randomUUID();
        String username = service.getUsernameById(id.toString());

        // ASSERT
        verify(mockSessionRepo, times(1)).getById(id);
        assertEquals("username", username);

    }

    /**
     * ensure that {@link SessionService#validateSessionId(String)
     * SessionService.validateSessionId} invokes getById on the repo layer and
     * returns true if the resulting username is not null
     */
    @Test
    public void validateSessionId_whenInvokedWithValidArgs_getsUsernameAndReturnsTrue() {

        // MOCK
        Session session = new Session();
        session.setUsername("username");

        when(mockSessionRepo.getById(any(UUID.class))).thenReturn(session);

        // ACT
        UUID id = UUID.randomUUID();
        Boolean valid = service.validateSessionId(id.toString());

        // ASSERT
        verify(mockSessionRepo, times(1)).getById(id);
        assertTrue(valid);

    }

    // #endregion

    // #region Helper

    /**
     * ensure when a valid UUID is passed the validator function returns True
     */
    @Test
    public void isValidUUID_whenInvokedWithValidUUID_thenReturnsTrue() {

        // MOCK

        // ACT
        Boolean result = service.isValidUUID(UUID.randomUUID().toString());

        // ASSERT
        assertTrue(result);

    }

    /**
     * ensure when an invalid UUID is passed the validator function returns False
     */
    @Test
    public void isValidUUID_whenInvokedWithInvalidUUID_thenReturnsFalse() {

        // MOCK

        // ACT
        Boolean result = service.isValidUUID(UUID.randomUUID().toString().substring(1));

        // ASSERT
        assertFalse(result);

    }

    /**
     * ensure that when a null reference is passed an exception is thrown
     */
    @Test
    public void isValidUUID_whenInvokedWithNullReference_thenThrowsException() {

        // MOCK

        // ACT

        // ASSERT
        assertThrows(NullPointerException.class, () -> {
            service.isValidUUID(null);
        });

    }

    // #endregion
}
