package com.jacob.backend.repository.interfaces;

import java.util.List;
import java.util.UUID;

import com.jacob.backend.data.Model.Game;

public interface GameRepositoryInterface {
    public List<Game> getAllByUserId(UUID userId);

    public void save(Game game);
}
