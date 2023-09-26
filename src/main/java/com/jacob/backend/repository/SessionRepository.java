package com.jacob.backend.repository;

import java.util.UUID;

import org.springframework.stereotype.Repository;

import com.jacob.backend.data.Model.Session;
import com.jacob.backend.repository.interfaces.SessionRepositoryInterface;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.TypedQuery;
import jakarta.transaction.Transactional;
import lombok.extern.apachecommons.CommonsLog;

@Repository
@CommonsLog
public class SessionRepository implements SessionRepositoryInterface {

    @PersistenceContext
    private EntityManager entityManager;

    @Override
    public Session getById(UUID sessionId) {
        try {
            return entityManager.find(Session.class, sessionId);
        } catch (Exception e) {
            log.error("Failed to get 'Session' from database", e);
            throw e;
        }
    }

    @Override
    public Session getByUsername(String username) {
        try {
            String qString = "SELECT s FROM Session s WHERE s.username LIKE :username";
            TypedQuery<Session> query = entityManager.createQuery(qString, Session.class);
            return query.setParameter("username", username).getResultStream().findFirst().orElse(null);
        } catch (Exception e) {
            log.error("Failed to get 'Session' from database", e);
            throw e;
        }
    }

    @Override
    public boolean sessionExistsForUsername(String username) {
        try {
            String qString = "SELECT s FROM Session s WHERE s.username LIKE :username";
            TypedQuery<Session> query = entityManager.createQuery(qString, Session.class);
            return query.setParameter("username", username).getResultList().size() > 0;
        } catch (Exception e) {
            log.error("Failed to get 'Session' from database", e);
            throw e;
        }
    }

    @Override
    @Transactional
    public void save(Session session) {
        try {
            entityManager.persist(session);
        } catch (Exception e) {
            log.error("Failed to save 'Session' to database", e);
            throw e;
        }
    }

    @Override
    @Transactional
    public void update(Session session) {
        try {
            entityManager.merge(session);
        } catch (Exception e) {
            log.error("Failed to update 'Session' in database", e);
            throw e;
        }
    }

    @Override
    @Transactional
    public void deleteById(UUID sessionId) {
        try {
            Session s = getById(sessionId);
            if (s != null) {
                entityManager.remove(s);
            }
        } catch (Exception e) {
            log.error("Failed to delete 'Session' from database", e);
            throw e;
        }
    }

    @Override
    @Transactional
    public void deleteByUsername(String username) {
        try {
            Session s = getByUsername(username);
            if (s != null) {
                entityManager.remove(s);
            }
        } catch (Exception e) {
            // Logger.error(e);
            throw e;
        }
    }
}
