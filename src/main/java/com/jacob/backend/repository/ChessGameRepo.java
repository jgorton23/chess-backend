package com.jacob.backend.repository;

import org.springframework.stereotype.Repository;

import com.jacob.backend.data.Game;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.transaction.Transactional;

@Repository
public class ChessGameRepo {
    @PersistenceContext
    private EntityManager em;

    @Transactional
    public void save(Game g) {
        em.persist(g);
    }
}
