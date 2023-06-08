package com.jacob.backend.repository;

import java.util.UUID;

import org.springframework.stereotype.Repository;

import com.jacob.backend.data.Model.User;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.TypedQuery;

@Repository
public class UserRepository implements UserRepositoryInterface {

    @PersistenceContext
    private EntityManager entityManager;

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
}
