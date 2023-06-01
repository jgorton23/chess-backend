package com.jacob.backend.repository;

import org.springframework.stereotype.Repository;

@Repository
public class ChessAuthRepository implements ChessAuthRepositoryInterface {

    public boolean login(String username, String pass) {
        return true;
        // return String.join(" ", new String[] { username, pass });
    }

    public boolean register(String username, String email, String passHash, String passSalt) {
        return true;
        // 
        // return String.join(" ", new String[] { username, email, passHash, passSalt });
    }

    public boolean userExists(String username) {
        return false;
    }

    public String getUserHash(String username) {
        return "";
    }

    public String getUserSalt(String username) {
        return "";
    }

}
