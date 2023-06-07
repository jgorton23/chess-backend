package com.jacob.backend.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.jacob.backend.data.Game;
import com.jacob.backend.repository.ChessGameRepo;

@Service
public class ChessGameService {
    @Autowired
    private ChessGameRepo gameRepo;

    public void createGame(Game game) {
        gameRepo.save(game);
    }
}
