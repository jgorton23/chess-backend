package com.jacob.backend.repository.interfaces;

import java.util.List;
import java.util.UUID;

import com.jacob.backend.data.Model.Game;

public interface GameRepositoryInterface {

    public Game getById(UUID gameId);

    public List<Game> getAllByUserId(UUID userId);

    public void save(Game game);

    public void update(Game game);
}
