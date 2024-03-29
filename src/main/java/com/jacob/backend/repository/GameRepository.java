package com.jacob.backend.repository;

import java.util.List;
import java.util.UUID;

import org.springframework.stereotype.Repository;

import com.jacob.backend.data.Model.Game;
import com.jacob.backend.repository.interfaces.GameRepositoryInterface;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.TypedQuery;
import jakarta.transaction.Transactional;
import lombok.extern.apachecommons.CommonsLog;

@Repository
@CommonsLog
public class GameRepository implements GameRepositoryInterface {

    @PersistenceContext
    EntityManager entityManager;

    public Game getById(UUID gameId) {
        try {
            return entityManager.find(Game.class, gameId);
        } catch (Exception e) {
            log.error("Failed to get 'Game' from database", e);
            throw e;
        }
    }

    public List<Game> getAllByUserId(UUID userId) {
        try {
            String qString = "SELECT g FROM Game g WHERE g.whitePlayerId = :userId OR g.blackPlayerId = :userId";
            TypedQuery<Game> query = entityManager.createQuery(qString, Game.class);
            return query.setParameter("userId", userId).getResultList();
        } catch (Exception e) {
            log.error("Failed to get 'Games' from database", e);
            throw e;
        }
    }

    @Transactional
    public void save(Game game) {
        try {
            entityManager.persist(game);
        } catch (Exception e) {
            log.error("Failed to save 'Game' to database", e);
            throw e;
        }
    }

    @Transactional
    public void update(Game game) {
        try {
            entityManager.merge(game);
        } catch (Exception e) {
            log.error("Failed to update 'Game' in database", e);
            throw e;
        }
    }
}
