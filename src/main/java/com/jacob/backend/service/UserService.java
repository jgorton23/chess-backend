package com.jacob.backend.service;

import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.jacob.backend.data.Model.User;
import com.jacob.backend.repository.UserRepositoryInterface;

@Service
public class UserService {

    @Autowired
    private UserRepositoryInterface userRepo;

    public User findById(UUID userId) {
        return userRepo.getById(userId);
    }

    public User findByUsername(String username) {
        return userRepo.getByUsername(username);
    }
}
