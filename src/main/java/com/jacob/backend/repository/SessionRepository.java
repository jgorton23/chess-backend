package com.jacob.backend.repository;

import java.util.UUID;

import org.springframework.stereotype.Repository;

import com.jacob.backend.data.Model.Session;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.transaction.Transactional;

@Repository
public class SessionRepository implements SessionRepositoryInterface {
    @PersistenceContext
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

    @Transactional
    public void update(Session session) {
        try {
            entityManager.merge(session);
        } catch (Exception e) {
            // Logger.error(e);
            throw e;
        }
    }

    @Transactional
    public void deleteById(UUID sessionId) {
        try {
            Session s = getById(sessionId);
            if (s != null) {
                entityManager.remove(s);
            }
        } catch (Exception e) {
            // Logger.error(e);
            throw e;
        }
    }
}
