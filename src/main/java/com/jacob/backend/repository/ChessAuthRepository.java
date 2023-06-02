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

    public boolean login(CredentialsDTO cred) {
        return true;
    }

    @Transactional
    public boolean register(User user) {
        entityManager.persist(user);
        return true;
    }

    public boolean userExists(String username) {
        TypedQuery<User> query = entityManager.createQuery(
                "SELECT u FROM Users u WHERE u.username LIKE :username", User.class);
        return query.setParameter("username", username).getResultList().size() > 0;
    }

    public String getUserHash(String username) {
        return "";
    }

    public String getUserSalt(String username) {
        return "";
    }

}
