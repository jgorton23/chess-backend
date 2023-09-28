package com.jacob.backend.repository;

import java.util.List;
import java.util.UUID;

import org.springframework.stereotype.Repository;

import com.jacob.backend.data.Model.Friend;
import com.jacob.backend.repository.interfaces.FriendRepositoryInterface;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.TypedQuery;
import jakarta.transaction.Transactional;
import lombok.extern.apachecommons.CommonsLog;

/**
 * Class for interacting with the 'Friend' database table, and managing
 * friendships between users
 */
@Repository
@CommonsLog
public class FriendRepository implements FriendRepositoryInterface {

    /**
     * Handles persistence of {@link Friend} objects
     */
    @PersistenceContext
    EntityManager entityManager;

    @Override
    public List<Friend> getById(UUID id) {
        try {
            String qString = "SELECT f FROM Friend f WHERE (f.userAId = :userId OR f.userBId = :userId)";
            TypedQuery<Friend> query = entityManager.createQuery(qString, Friend.class);
            List<Friend> f = query.setParameter("userId", id).getResultList();
            return f;
        } catch (Exception e) {
            log.error("Failed to get 'Friends' from database", e);
            throw e;
        }
    }

    @Override
    public Friend getByIds(UUID userId, UUID friendId) {
        try {
            String qString = "SELECT f FROM Friend f WHERE (f.userAId = :userId AND f.userBId = :friendId) OR (f.userAId = :friendId AND f.userBId = :userId)";
            TypedQuery<Friend> query = entityManager.createQuery(qString, Friend.class);
            Friend f = query
                    .setParameter("userId", userId)
                    .setParameter("friendId", friendId)
                    .getResultStream()
                    .findFirst()
                    .orElse(null);
            return f;
        } catch (Exception e) {
            log.error("Failed to get 'Friend' from database", e);
            throw e;
        }
    }

    @Override
    @Transactional
    public void save(Friend friend) {
        try {
            entityManager.persist(friend);
        } catch (Exception e) {
            log.error("Failed to save 'Friend' to databse", e);
            throw e;
        }
    }

    @Override
    @Transactional
    public void update(Friend friend) {
        try {
            entityManager.merge(friend);
        } catch (Exception e) {
            log.error("Failed to update 'Friend' in database", e);
            throw e;
        }
    }

    @Override
    @Transactional
    public void delete(Friend friend) {
        try {
            entityManager.remove(friend);
        } catch (Exception e) {
            log.error("Failed to delete 'Friend' from database", e);
            throw e;
        }
    }

}
