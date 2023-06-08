package com.jacob.backend.repository;

import org.springframework.stereotype.Repository;

import com.jacob.backend.data.CredentialsDTO;
import com.jacob.backend.data.User;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.TypedQuery;
import jakarta.transaction.Transactional;

@Repository
public class ChessAuthRepository implements ChessAuthRepositoryInterface {

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
