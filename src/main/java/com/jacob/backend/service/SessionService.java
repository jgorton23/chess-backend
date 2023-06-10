package com.jacob.backend.service;

import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.jacob.backend.data.Model.Session;
import com.jacob.backend.repository.interfaces.SessionRepositoryInterface;

@Service
public class SessionService {

    @Autowired
    private SessionRepositoryInterface sessionRepo;

    public Session findById(UUID sessionId) {
        return sessionRepo.getById(sessionId);
    }

    public String create(String username) {
        sessionRepo.deleteByUsername(username);
        Session session = new Session();
        session.setUsername(username);
        sessionRepo.save(session);
        return session.getId();
    }

    public void delete(UUID sessionId) {
        sessionRepo.deleteById(sessionId);
    }

    public void update(UUID sessionId, String username) {
        Session session = sessionRepo.getById(sessionId);
        session.setUsername(username);
        sessionRepo.update(session);
    }
}
