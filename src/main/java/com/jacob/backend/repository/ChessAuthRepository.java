package com.jacob.backend.repository;

import java.util.Objects;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.jacob.backend.data.CredentialsDTO;
import com.jacob.backend.data.User;

import jakarta.annotation.PostConstruct;
import jakarta.persistence.EntityManager;
import jakarta.persistence.Persistence;
import jakarta.persistence.PersistenceContext;
import jakarta.transaction.Transactional;

@Repository
public class ChessAuthRepository implements ChessAuthRepositoryInterface {

    @PersistenceContext
    private EntityManager entityManager;

    public boolean login(CredentialsDTO cred) {
        return true;
    }

    @Transactional
    public boolean register(User user) {
        entityManager.persist(user);
        return true;
    }

    public boolean userExists(String username) {
        User u;
        u = entityManager.find(User.class, UUID.randomUUID());
        return u != null;

    }

    public String getUserHash(String username) {
        return "";
    }

    public String getUserSalt(String username) {
        return "";
    }

}
