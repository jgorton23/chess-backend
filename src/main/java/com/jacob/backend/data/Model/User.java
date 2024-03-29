package com.jacob.backend.data.Model;

import java.util.UUID;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.NamedQuery;
import jakarta.persistence.Table;

@Entity
@Table(name = "users")
@NamedQuery(name = "getAllUsers", query = "SELECT u FROM User u")
public class User {

    @Id
    @GeneratedValue
    private UUID id;

    private String username;

    private String email;

    private String passwordHash;

    private String passwordSalt;

    public User() {
    }

    public User(String username, String email, String passHash, String passSalt) {
        this.username = username;
        this.email = email;
        passwordHash = passHash;
        passwordSalt = passSalt;
    }

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPasswordHash() {
        return passwordHash;
    }

    public void setPasswordHash(String hash) {
        passwordHash = hash;
    }

    public String getPasswordSalt() {
        return passwordSalt;
    }

    public void setPasswordSalt(String salt) {
        passwordSalt = salt;
    }

}
