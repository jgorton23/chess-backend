package com.jacob.backend.repository;

import org.springframework.boot.autoconfigure.ldap.embedded.EmbeddedLdapProperties.Credential;

import com.jacob.backend.data.CredentialsDTO;
import com.jacob.backend.data.User;

public interface ChessAuthRepositoryInterface {
    /**
     * logs a user in using the given credentials
     * 
     * @param cred the credentials of the user attempting to log in
     * @return true if the user successfully logs in, otherwise false
     */
    public boolean login(CredentialsDTO cred);

    /**
     * Registers a new user in the database
     * 
     * @param user the user to be registered
     * @return true if the user was successfully registered, otherwise false
     */
    public boolean register(User user);

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
