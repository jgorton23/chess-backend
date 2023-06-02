package com.jacob.backend.repository;

import java.util.UUID;

import org.springframework.stereotype.Repository;

import com.jacob.backend.data.CredentialsDTO;
import com.jacob.backend.data.User;

import jakarta.persistence.EntityManager;
import jakarta.persistence.Persistence;
import jakarta.persistence.PersistenceContext;

@Repository
public class ChessAuthRepository implements ChessAuthRepositoryInterface {

    @PersistenceContext(unitName = "mypu")
    private EntityManager entityManager;

    public boolean login(CredentialsDTO cred) {
        return true;
    }

    public boolean register(User user) {
        entityManager.persist(user);
        return true;
    }

    public boolean userExists(String username) {
        // User u = entityManager.find(User.class, username);
        User u = null;
        try {
            EntityManager em = Persistence.createEntityManagerFactory("mypu").createEntityManager();
            u = em.find(User.class, UUID.randomUUID());
        } catch (Exception e) {
            e.printStackTrace();
        }
        return u != null;

    }

    public String getUserHash(String username) {
        return "";
    }

    public String getUserSalt(String username) {
        return "";
    }

}
