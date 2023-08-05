package com.jacob.backend.service.IntegrationTests;

import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
@AutoConfigureMockMvc
@Tag("IntegrationTest")
public class SessionServiceIntegrationTest {

    @Test
    public void integrationTest() {
        assertTrue(false, "message");
    }
}
