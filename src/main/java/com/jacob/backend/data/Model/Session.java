package com.jacob.backend.data.Model;

import java.util.UUID;

import jakarta.json.JsonObject;

import com.jacob.backend.data.JsonConvertible;
import com.jacob.backend.responses.JSONResponses;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "sessions")
public class Session implements JsonConvertible {
    @Id
    @GeneratedValue
    private UUID id;

    private String username;

    private UUID currentGameId;

    public Session() {
    }

    public String getId() {
        return id.toString();
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

    public UUID getCurrentGameId() {
        return currentGameId;
    }

    public void setCurrentGameId(UUID id) {
        this.currentGameId = id;
    }

    public JsonObject toJson() {
        return JSONResponses.objectBuilder()
                .add("id", id.toString())
                .add("username", username)
                .add("currentGame", currentGameId.toString())
                .build();
    }
}
