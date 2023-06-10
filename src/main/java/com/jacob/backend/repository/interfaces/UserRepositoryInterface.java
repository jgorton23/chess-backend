package com.jacob.backend.repository.interfaces;

import java.util.UUID;

import com.jacob.backend.data.Model.User;

public interface UserRepositoryInterface {

    /**
     * Registers a new user in the database
     * 
     * @param user the user to be registered
     */
    public void save(User user);

    /**
     * Updates the user to have the given properties
     * 
     * @param user the user to update
     */
    public void update(User user);

    /**
     * Returns whether or not a user exists with the given username
     * 
     * @param username the username to check
     * @return true if a user exists, else false
     */
    public boolean userExists(String username);

    /**
     * returns the user object associated with the given Id
     * 
     * @param userId the UUID of the user to get
     * @return the User
     */
    public User getById(UUID userId);

    /**
     * returns the user object associated with the given username
     * 
     * @param username the Username of the user to get
     * @return the User
     */
    public User getByUsername(String username);

    /**
     * returns the password hash for the given user
     * 
     * @param username the username of the user whose hash we want
     * @return the hash
     */
    public String getUserHash(String username);

    /**
     * returns teh password salt for the given user
     * 
     * @param username the username of the user whose salt we want
     * @return the salt
     */
    public String getUserSalt(String username);
}
