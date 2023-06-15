package com.jacob.backend.repository;

import java.util.List;
import java.util.UUID;

import org.springframework.stereotype.Repository;

import com.jacob.backend.data.Model.Game;
import com.jacob.backend.repository.interfaces.GameRepositoryInterface;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.TypedQuery;

@Repository
public class GameRepository implements GameRepositoryInterface {

    @PersistenceContext
    EntityManager entityManager;

    public List<Game> getAllByUserId(UUID userId) {
        try {
            String qString = "SELECT g FROM Game g WHERE g.whitePlayerId = :userId OR g.blackPlayerId = :userId";
            TypedQuery<Game> query = entityManager.createQuery(qString, Game.class);
            return query.setParameter("userId", userId).getResultList();
        } catch (Exception e) {
            // Logger.error(e);
            throw e;
        }
    }

    public void save(Game game) {
        try {
            entityManager.persist(game);
        } catch (Exception e) {
            // Logger.error(e);
            throw e;
        }
    }
}
