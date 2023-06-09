package com.jacob.backend.service;

import java.util.Random;

import org.apache.commons.codec.digest.DigestUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.jacob.backend.data.DTO.CredentialsDTO;
import com.jacob.backend.data.Model.User;
import com.jacob.backend.repository.UserRepositoryInterface;
import com.jacob.backend.responses.AlreadyFoundException;
import com.jacob.backend.responses.InvalidCredentialsException;
import com.jacob.backend.responses.MissingFieldException;
import com.jacob.backend.responses.PasswordMismatchException;

@Service
public class ChessAuthService {

    @Autowired
    private UserRepositoryInterface userRepo;

    @Autowired
    private SessionService sessionService;

    public String login(CredentialsDTO cred) {

        String username = cred.getUsername();
        String pass = cred.getPassword();

        if (username == null) {
            throw new MissingFieldException("Login Credentials", "username");
        } else if (pass == null) {
            throw new MissingFieldException("Login Credentials", "password");
        } else if (!userRepo.userExists(username)) {
            throw new InvalidCredentialsException("username");
        }

        String passwordHash = userRepo.getUserHash(username);
        String passwordSalt = userRepo.getUserSalt(username);
        String passwordHashAttempt = getPasswordHash(pass + passwordSalt);

        if (!passwordHashAttempt.equals(passwordHash)) {
            throw new InvalidCredentialsException("password");
        }

        return sessionService.create(username);
    }

    public void register(CredentialsDTO cred) {

        String username = cred.getUsername();
        String pass = cred.getPassword();
        String passConfirm = cred.getConfirm();
        String email = cred.getEmail();

        if (username == null) {
            throw new MissingFieldException("Login Credentials", "username");
        } else if (pass == null) {
            throw new MissingFieldException("Login Credentials", "password");
        } else if (passConfirm == null) {
            throw new MissingFieldException("Login Credentials", "confirm");
        } else if (userRepo.userExists(username)) {
            throw new AlreadyFoundException("User", "username: " + username);
        } else if (!pass.equals(passConfirm)) {
            throw new PasswordMismatchException(pass, passConfirm);
        }

        String salt = getRandomString(20);
        String hash = getPasswordHash(pass + salt);

        userRepo.save(new User(username, email, hash, salt));
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
