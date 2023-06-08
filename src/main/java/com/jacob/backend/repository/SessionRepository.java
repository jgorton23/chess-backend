package com.jacob.backend.repository;

import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.jacob.backend.data.Session;

import jakarta.persistence.EntityManager;
import jakarta.transaction.Transactional;

@Repository
public class SessionRepository implements SessionRepositoryInterface {
    @Autowired
    private EntityManager entityManager;

    public Session getById(UUID sessionId) {
        try {
            return entityManager.find(Session.class, sessionId);
        } catch (Exception e) {
            // Logger.error(e)
            throw e;
        }
    }

    @Transactional
    public void save(Session session) {
        try {
            entityManager.persist(session);
        } catch (Exception e) {
            // Logger.error(e);
            throw e;
        }
    }
}
