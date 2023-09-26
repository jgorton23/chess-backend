package com.jacob.backend.repository;

import java.util.UUID;

import org.springframework.stereotype.Repository;

import com.jacob.backend.data.Model.User;
import com.jacob.backend.repository.interfaces.UserRepositoryInterface;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.TypedQuery;
import jakarta.transaction.Transactional;
import lombok.extern.apachecommons.CommonsLog;

@Repository
@CommonsLog
public class UserRepository implements UserRepositoryInterface {

    @PersistenceContext
    private EntityManager entityManager;

    @Override
    @Transactional
    public void save(User user) {
        try {
            entityManager.persist(user);
        } catch (Exception e) {
            log.error("Failed to save 'User' to database", e);
            throw e;
        }
    }

    @Override
    @Transactional
    public void update(User user) {
        try {
            entityManager.merge(user);
        } catch (Exception e) {
            log.error("Failed to update 'User' in database", e);
            throw e;
        }
    }

    @Override
    public boolean userExists(String username) {
        try {
            String qString = "SELECT u FROM User u WHERE u.username LIKE :username";
            TypedQuery<User> query = entityManager.createQuery(qString, User.class);
            return query.setParameter("username", username).getResultList().size() > 0;
        } catch (Exception e) {
            log.error("Failed to get 'User' from database", e);
            throw e;
        }
    }

    @Override
    public User getById(UUID userId) {
        try {
            return entityManager.find(User.class, userId);
        } catch (Exception e) {
            log.error("Failed to get 'User' from database", e);
            throw e;
        }
    }

    @Override
    public User getByUsername(String username) {
        try {
            String qString = "SELECT u FROM User u WHERE u.username LIKE :username";
            TypedQuery<User> query = entityManager.createQuery(qString, User.class);
            return query.setParameter("username", username).getSingleResult();
        } catch (Exception e) {
            log.error("Failed to get 'User.username' from database", e);
            throw e;
        }
    }

    @Override
    public String getUserHash(String username) {
        try {
            String qString = "SELECT u.passwordHash FROM User u WHERE u.username LIKE :username";
            TypedQuery<String> query = entityManager.createQuery(qString, String.class);
            return query.setParameter("username", username).getSingleResult();
        } catch (Exception e) {
            log.error("Failed to get 'User.passwordHash' from database", e);
            throw e;
        }
    }

    @Override
    public String getUserSalt(String username) {
        try {
            String qString = "SELECT u.passwordSalt FROM User u WHERE u.username LIKE :username";
            TypedQuery<String> query = entityManager.createQuery(qString, String.class);
            return query.setParameter("username", username).getSingleResult();
        } catch (Exception e) {
            log.error("Failed to get 'User.passwordSalt' from database", e);
            throw e;
        }
    }
}
