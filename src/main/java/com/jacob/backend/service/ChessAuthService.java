package com.jacob.backend.service;

import java.util.Random;

import org.apache.commons.codec.digest.DigestUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.jacob.backend.data.CredentialsDTO;
import com.jacob.backend.data.User;
import com.jacob.backend.repository.ChessAuthRepositoryInterface;

@Service
public class ChessAuthService {

    @Autowired
    private ChessAuthRepositoryInterface authRepo;

    public String login(CredentialsDTO cred) {
        String username = cred.getUsername();
        if (!authRepo.userExists(username)) {
            return "Invalid username";
        }

        String pass = cred.getPassword();
        String salt = authRepo.getUserSalt(username);

        String passwordHashAttempt = getPasswordHash(pass + salt);
        String passwordHash = authRepo.getUserHash(username);

        if (!passwordHashAttempt.equals(passwordHash)) {
            return "Invalid password";
        }

        boolean success = authRepo.login(new CredentialsDTO(username, pass));

        if (success) {
            return "Successfully logged in";
        } else {
            return "An error occurred";
        }
    }

    public String register(CredentialsDTO cred) {
        String username = cred.getUsername();
        boolean exists = authRepo.userExists(username);
        if (exists) {
            return "User already exists";
        }

        String pass = cred.getPassword();
        String passConfirm = cred.getConfirm();

        if (pass == null && passConfirm == null) {
            return "Missing Password  Field";
        } else if (pass == null || !pass.equals(passConfirm)) {
            return "Passwords Don't Match: " + pass + " " + passConfirm;
        }

        String email = cred.getEmail();

        String salt = getRandomString(20);

        String hash = getPasswordHash(pass + salt);

        boolean success = authRepo.register(new User(username, email, hash, salt));

        if (success) {
            return "Successfully registered";
        } else {
            return "An error occurred";
        }
    }

    private String getRandomString(int length) {
        Random rand = new Random();
        String chars = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";
        char[] salt = new char[length];
        for (int i = 0; i < length; i++) {
            salt[i] = chars.charAt(rand.nextInt(chars.length()));
        }
        return new String(salt);
    }

    private String getPasswordHash(String saltedPass) {
        return DigestUtils.sha256Hex(saltedPass);
    }

}
