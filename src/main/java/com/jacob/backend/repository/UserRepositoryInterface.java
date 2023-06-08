package com.jacob.backend.repository;

import java.util.UUID;

import com.jacob.backend.data.Model.User;

public interface UserRepositoryInterface {
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
}
