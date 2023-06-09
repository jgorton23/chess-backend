package com.jacob.backend.service;

import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.jacob.backend.data.Model.Session;
import com.jacob.backend.repository.interfaces.SessionRepositoryInterface;
import com.jacob.backend.responses.exceptions.UnauthorizedException;

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
     * Gets the username associated with the given sessionId if one exists
     * 
     * @param sessionId the String UUID associated with the Session to get
     * @return the username assocaiated with the given SessionId if one exists
     * @throws UnauthorizedException if the given sessionId is not valid
     */
    public String getUsernameById(String sessionId) throws UnauthorizedException {
        if (!isValidUUID(sessionId)) {
            throw new UnauthorizedException();
        }
        Session s = findById(UUID.fromString(sessionId));
        if (s == null) {
            throw new UnauthorizedException();
        }

        return s.getUsername();
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
