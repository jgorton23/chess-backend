package com.jacob.backend.data.Model;

import java.util.UUID;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "friends")
public class Friend {
    @Id
    @GeneratedValue
    private UUID id;

    private UUID userAId;

    private UUID userBId;

    private boolean pending;

    public Friend() {
    }

    public UUID getUserAId() {
        return userAId;
    }

    public void setUserAId(UUID id) {
        this.userAId = id;
    }

    public UUID getUserBId() {
        return userBId;
    }

    public void setUserBId(UUID id) {
        this.userBId = id;
    }

    public boolean getPending() {
        return pending;
    }

    public void setPending(boolean pending) {
        this.pending = pending;
    }
}
