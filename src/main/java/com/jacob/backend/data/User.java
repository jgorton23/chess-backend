package com.jacob.backend.data;

import java.util.UUID;
import jakarta.persistence.Id;

public class User {

    @Id
    private UUID id;

    private String username;

    private String email;

    private String passwordHash;

    private String passwordSalt;

    public User(String username, String email, String passHash, String passSalt) {
        this.username = username;
        this.email = email;
        passwordHash = passHash;
        passwordSalt = passSalt;
    }

    public String getUsername() {
        return username;
    }

    public String getEmail() {
        return email;
    }

    public String getPasswordHash() {
        return passwordHash;
    }

    public String getPasswordSalt() {
        return passwordSalt;
    }

}
