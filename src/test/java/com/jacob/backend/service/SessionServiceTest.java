package com.jacob.backend.service;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.UUID;

import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.mock.mockito.MockBean;

import com.jacob.backend.data.Model.Session;
import com.jacob.backend.repository.interfaces.SessionRepositoryInterface;

@Tag("UnitTest")
public class SessionServiceTest {

    @MockBean
    private SessionRepositoryInterface mockSessionRepo;

    // #region CRUD

    @Test
    public void findById_whenInvoked_invokesRepo() {
        when(mockSessionRepo.getById(any(UUID.class))).thenReturn(new Session());
        verify(mockSessionRepo, times(1)).getById(any(UUID.class));
    }

    // #endregion

    // #region Helper

    @Test
    public void isValidUUID_whenInvokedWithValidUUID_thenReturnsTrue() {
        assertTrue(false, "");
    }

    // #endregion
}
