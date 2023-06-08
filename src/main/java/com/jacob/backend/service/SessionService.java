package com.jacob.backend.service;

import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.jacob.backend.data.Session;
import com.jacob.backend.repository.SessionRepositoryInterface;

@Service
public class SessionService {
    @Autowired
    private SessionRepositoryInterface sessionRepo;

    public Session findById(UUID sessionId) {
        return sessionRepo.getById(sessionId);
    }

    public String create(String username) {
        Session session = new Session();
        session.setUsername(username);
        sessionRepo.save(session);
        return session.getId();
    }

    public void delete(UUID sessionId) {
        sessionRepo.deleteById(sessionId);
    }
}
