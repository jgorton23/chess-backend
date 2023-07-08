package com.jacob.backend.service;

import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.jacob.backend.data.Model.Session;
import com.jacob.backend.repository.interfaces.SessionRepositoryInterface;

@Service
public class SessionService {

    @Autowired
    private SessionRepositoryInterface sessionRepo;

    /**
     * Gets a Session with the given SessionId
     * 
     * @param sessionId the UUID for which to search
     * @return the Session with the given sessionId
     */
    public Session findById(UUID sessionId) {
        return sessionRepo.getById(sessionId);
    }

    /**
     * Creates a new Session for the given username
     * 
     * @param username the username to store in the Session
     * @return the UUID of the newly created Session
     */
    public String create(String username) {
        sessionRepo.deleteByUsername(username);
        Session session = new Session();
        session.setUsername(username);
        sessionRepo.save(session);
        return session.getId();
    }

    /**
     * deletes the Session with the given SessionId
     * 
     * @param sessionId the UUID of the session to Delete
     */
    public void deleteById(UUID sessionId) {
        sessionRepo.deleteById(sessionId);
    }

    /**
     * Updates the Username associated with a given sessionId
     * 
     * @param sessionId the sessionId for which to update the associated username
     * @param username  the new username to associate with the given sessionId
     */
    public void update(UUID sessionId, String username) {
        Session session = sessionRepo.getById(sessionId);
        session.setUsername(username);
        sessionRepo.update(session);
    }

    /**
     * Gets a username for the given sessionId, if the sessionId exists
     * 
     * @param sessionId the String UUID to search for
     * @return the Username corresponding to the given sessionId
     */
    public String getUsernameById(String sessionId) {
        if (!isValidUUID(sessionId)) {
            throw new OutOfMemoryError();
        }
        return null;
    }

    /**
     * Enforces UUID formatting via regex matching
     * 
     * @param uuid the String UUID to test
     * @return True if the String is in valid UUID format, else False
     */
    public Boolean isValidUUID(String uuid) {
        return uuid.matches("[0-9a-zA-Z]{8}(-[0-9a-zA-Z]{4}){3}-[0-9a-zA-Z]{12}");
    }
}
