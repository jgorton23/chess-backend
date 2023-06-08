package com.jacob.backend.repository;

import com.jacob.backend.data.CredentialsDTO;
import com.jacob.backend.data.User;

public interface ChessAuthRepositoryInterface {

    /**
     * Registers a new user in the database
     * 
     * @param user the user to be registered
     */
    public void save(User user);

    /**
     * Returns whether or not a user exists with the given username
     * 
     * @param username the username to check
     * @return true if a user exists, else false
     */
    public boolean userExists(String username);

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
