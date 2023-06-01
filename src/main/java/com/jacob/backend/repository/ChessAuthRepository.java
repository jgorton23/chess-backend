package com.jacob.backend.repository;

import org.springframework.stereotype.Repository;

@Repository
public class ChessAuthRepository implements ChessAuthRepositoryInterface {

    public String login(String username, String pass) {
        return "login repo: " + username + " " + pass;
    }

    public String register(String username, String email, String passHash, String passSalt) {
        return String.join(" ", new String[] { username, email, passHash, passSalt });
    }

    public boolean userExists(String username) {
        return false;
    }

}
