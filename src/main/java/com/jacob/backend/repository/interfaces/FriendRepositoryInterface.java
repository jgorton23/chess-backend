package com.jacob.backend.repository.interfaces;

import java.util.List;
import java.util.UUID;

import com.jacob.backend.data.Model.Friend;

public interface FriendRepositoryInterface {

    /**
     * gets the friends of the user with the given UUID
     * 
     * @param id the UUID of the user
     * @return a list of the users Friend relationships
     */
    public List<Friend> getById(UUID id);

    /**
     * returns the Friend object associated with the 2 users, if it exists
     * 
     * @param userId   the first users UUID
     * @param friendId the second users UUID
     * @return the Friend object
     */
    public Friend getByIds(UUID userId, UUID friendId);

    /**
     * saves the given friend relationship in the database
     * 
     * @param friend - the relationship to save
     */
    public void save(Friend friend);

    /**
     * updates the given Friend object
     * 
     * @param friend the Friend to update
     */
    public void update(Friend friend);

    /**
     * deletes the given Friend object
     * 
     * @param friend the Friend object to delete
     */
    public void delete(Friend friend);
}
