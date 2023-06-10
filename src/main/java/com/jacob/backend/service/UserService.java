package com.jacob.backend.service;

import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.jacob.backend.data.DTO.CredentialsDTO;
import com.jacob.backend.data.DTO.ProfileDTO;
import com.jacob.backend.data.Model.User;
import com.jacob.backend.repository.interfaces.FriendRepositoryInterface;
import com.jacob.backend.repository.interfaces.UserRepositoryInterface;
import com.jacob.backend.responses.AlreadyFoundException;
import com.jacob.backend.responses.PasswordMismatchException;

@Service
public class UserService {

    @Autowired
    private UserRepositoryInterface userRepo;

    @Autowired
    private FriendRepositoryInterface friendRepo;

    @Autowired
    private ChessAuthService authService;

    public User findById(UUID userId) {
        return userRepo.getById(userId);
    }

    public User findByUsername(String username) {
        return userRepo.getByUsername(username);
    }

    public ProfileDTO getProfile(String username) {
        User u = findByUsername(username);
        UUID id = u.getId();
        int friends = friendRepo.getById(id).size();
        String email = findByUsername(username).getEmail();
        return new ProfileDTO(friends, username, email);
    }

    public void update(String username, CredentialsDTO creds) {
        String newUsername = creds.getUsername();
        String email = creds.getEmail();
        String password = creds.getPassword();
        String confirm = creds.getConfirm();

        User user = findByUsername(username);

        if (newUsername != null) {
            if (!userRepo.userExists(newUsername)) {
                user.setUsername(newUsername);
            } else {
                throw new AlreadyFoundException("User", "username: " + newUsername);
            }
        }

        if (email != null) {
            user.setEmail(email);
        }

        if (password != null && confirm != null) {
            if (password.equals(confirm)) {
                String salt = authService.getRandomString(20);
                String hash = authService.getPasswordHash(password + salt);

                user.setPasswordSalt(salt);
                user.setPasswordHash(hash);
            } else {
                throw new PasswordMismatchException(password, confirm);
            }
        }

        userRepo.update(user);
    }
}
