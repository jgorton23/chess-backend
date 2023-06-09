package com.jacob.backend.repository;

import java.util.UUID;

import com.jacob.backend.data.Model.Session;

public interface SessionRepositoryInterface {
    /**
     * Get the session associated with the given UUID
     * 
     * @param sessionId the UUID of the session to get
     * @return the Session object
     */
    public Session getById(UUID sessionId);

    /**
     * saves the Session object
     * 
     * @param session
     */
    public void save(Session session);

    /**
     * updates the session object
     * 
     * @param session
     */
    public void update(Session session);

    /**
     * Deletes the session with the given UUID
     * 
     * @param sessionId
     */
    public void deleteById(UUID sessionId);
}
