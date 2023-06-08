package com.jacob.backend.data.Model;

import java.util.UUID;

import javax.json.JsonObject;

import com.jacob.backend.responses.JSONResponses;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "sessions")
public class Session {
    @Id
    @GeneratedValue
    private UUID id;

    private String username;

    public Session() {
    }

    public String getId() {
        return id.toString();
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public JsonObject toJson() {
        return JSONResponses.objectBuilder().add("id", id.toString()).add("username", username).build();
    }
}
