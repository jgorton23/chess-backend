package com.jacob.backend.repository;

import org.springframework.stereotype.Repository;

import com.jacob.backend.data.CredentialsDTO;
import com.jacob.backend.data.User;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;

@Repository
public class ChessAuthRepository implements ChessAuthRepositoryInterface {

    @PersistenceContext
    private EntityManager entityManager;

    public boolean login(CredentialsDTO cred) {
        return true;
    }

    public boolean register(User user) {
        // entityManager.getTransaction().begin();
        entityManager.persist(user);
        // entityManager.getTransaction().commit();
        return true;
    }

    public boolean userExists(String username) {

        // User u = (User) entityManager.createQuery("SELECT user from users where user.username = ?1")
        //         .setParameter(1, username)
        //         .getSingleResult();
        User u = entityManager.find(User.class, username);
        return u != null;
    }

    public String getUserHash(String username) {
        return "";
    }

    public String getUserSalt(String username) {
        return "";
    }

}
