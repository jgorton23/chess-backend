package com.jacob.backend.repository;

import java.util.UUID;

import org.springframework.stereotype.Repository;

import com.jacob.backend.data.Model.User;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.TypedQuery;
import jakarta.transaction.Transactional;

@Repository
public class UserRepository implements UserRepositoryInterface {

    @PersistenceContext
    private EntityManager entityManager;

    @Transactional
    public void save(User user) {
        try {
            entityManager.persist(user);
        } catch (Exception e) {
            // Logger.error(e);
            throw e;
        }
    }

    @Transactional
    public void update(User user) {
        try {
            User old = getByUsername(user.getUsername());
            old.setEmail(user.getEmail());
            old.setUsername(user.getUsername());
            old.setPasswordHash(user.getPasswordHash());
            old.setPasswordSalt(user.getPasswordSalt());
        } catch (Exception e) {
            // Logger.error(e);
            throw e;
        }
    }

    public boolean userExists(String username) {
        try {
            String qString = "SELECT u FROM User u WHERE u.username LIKE :username";
            TypedQuery<User> query = entityManager.createQuery(qString, User.class);
            return query.setParameter("username", username).getResultList().size() > 0;
        } catch (Exception e) {
            // Logger.error(e);
            throw e;
        }
    }

    public User getById(UUID userId) {
        try {
            return entityManager.find(User.class, userId);
        } catch (Exception e) {
            // Logger.error(e)
            throw e;
        }
    }

    public User getByUsername(String username) {
        try {
            String qString = "SELECT u FROM User u WHERE u.username LIKE :username";
            TypedQuery<User> query = entityManager.createQuery(qString, User.class);
            return query.setParameter("username", username).getSingleResult();
        } catch (Exception e) {
            // Logger.error(e);
            throw e;
        }
    }

    public String getUserHash(String username) {
        try {
            String qString = "SELECT u.passwordHash FROM User u WHERE u.username LIKE :username";
            TypedQuery<String> query = entityManager.createQuery(qString, String.class);
            return query.setParameter("username", username).getSingleResult();
        } catch (Exception e) {
            // Logger.error(e);
            throw e;
        }
    }

    public String getUserSalt(String username) {
        try {
            String qString = "SELECT u.passwordSalt FROM User u WHERE u.username LIKE :username";
            TypedQuery<String> query = entityManager.createQuery(qString, String.class);
            return query.setParameter("username", username).getSingleResult();
        } catch (Exception e) {
            // Logger.error(e);
            throw e;
        }
    }
}
