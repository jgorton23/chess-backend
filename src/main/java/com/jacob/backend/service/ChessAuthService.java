package com.jacob.backend.service;

import java.util.Random;

import org.apache.commons.codec.digest.DigestUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.jacob.backend.data.CredentialsDTO;
import com.jacob.backend.repository.ChessAuthRepository;

@Service
public class ChessAuthService {

    @Autowired
    private ChessAuthRepository authRepo;

    public String login(CredentialsDTO cred) {
        String username = cred.getUsername();
        if (!authRepo.userExists(username)) {
            return "Invalid username";
        }

        String pass = cred.getPassword();
        return authRepo.login(username, pass);
    }

    public String register(CredentialsDTO cred) {
        String username = cred.getUsername();
        if (authRepo.userExists(username)) {
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

        return authRepo.register(username, email, hash, salt);
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
