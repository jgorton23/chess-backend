package com.jacob.backend.repository.interfaces;

import java.util.List;
import java.util.UUID;

import com.jacob.backend.data.Model.Game;

public interface GameRepositoryInterface {

    /**
     * Gets the game with the given {@code UUID}
     * 
     * @param gameId the UUID of the game to get from the database
     * @return the {@link Game} if it exists, otherwise null
     */
    public Game getById(UUID gameId);

    /**
     * Gets all of the games for the user with the given {@code UUID}
     * 
     * @param userId the UUID of the user for whom to get all games from the db
     * @return the list of {@link Game Games} for the user
     */
    public List<Game> getAllByUserId(UUID userId);

    /**
     * Saves the given {@link Game} in the database
     * 
     * @param game the game to save
     */
    public void save(Game game);

    /**
     * Updates the given {@link Game} in the database
     * 
     * @param game the game to update
     */
    public void update(Game game);
}
