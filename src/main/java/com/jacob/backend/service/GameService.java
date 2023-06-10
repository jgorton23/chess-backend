package com.jacob.backend.service;

import java.util.List;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.jacob.backend.data.Model.Game;
import com.jacob.backend.data.Model.User;
import com.jacob.backend.repository.interfaces.GameRepositoryInterface;
import com.jacob.backend.responses.NotFoundException;

@Service
public class GameService {

    @Autowired
    private GameRepositoryInterface gameRepo;

    @Autowired
    private UserService userService;

    public List<Game> findAllByUserId(UUID userId) {
        return gameRepo.getAllByUserId(userId);
    }

    public List<Game> findAllByUsername(String username) {
        User u = userService.findByUsername(username);
        if (u == null) {
            throw new NotFoundException("User", "username: " + username);
        }
        return findAllByUserId(u.getId());
    }
}
