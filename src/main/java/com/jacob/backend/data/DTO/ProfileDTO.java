package com.jacob.backend.data.DTO;

import javax.json.JsonObject;

import com.jacob.backend.data.JsonConvertible;
import com.jacob.backend.responses.JSONResponses;

public class ProfileDTO implements JsonConvertible {
    private int friends;

    private String username;

    private String email;

    public ProfileDTO(int friends, String username, String email) {
        this.friends = friends;
        this.username = username;
        this.email = email;
    }

    public ProfileDTO() {
        // this(0, null, null);
    }

    public int getFriends() {
        return friends;
    }

    public void setFriends(int friends) {
        this.friends = friends;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public JsonObject toJson() {
        return JSONResponses.objectBuilder()
                .add("friends", friends)
                .add("username", username)
                .add("email", email)
                .build();
    }
}
