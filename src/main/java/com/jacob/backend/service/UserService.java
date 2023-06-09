package com.jacob.backend.service;

import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.jacob.backend.data.DTO.CredentialsDTO;
import com.jacob.backend.data.DTO.ProfileDTO;
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

    public ProfileDTO getProfile(String username) {
        return null;
    }

    public void update(String username, CredentialsDTO creds) {
        String newUsername = creds.getUsername();
        String email = creds.getEmail();
        String password = creds.getPassword();
        String confirm = creds.getConfirm();

        userRepo.update(new User(newUsername, email, "testHash", "testSalt"));
    }
}
