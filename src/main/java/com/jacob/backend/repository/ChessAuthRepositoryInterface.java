package com.jacob.backend.repository;

public interface ChessAuthRepositoryInterface {
    /**
     * logs a user in using the given credentials
     * 
     * @param username the username of the user to log in
     * @param pass     the password of the user to log in
     * @return a status message
     */
    public boolean login(String username, String pass);

    /**
     * Registers a new user in the database
     * 
     * @param username the username of the new user
     * @param email    the email of the new user
     * @param passHash the password hash of the new user
     * @param passSalt the password salt of the new user
     * @return a status message
     */
    public boolean register(String username, String email, String passHash, String passSalt);

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
