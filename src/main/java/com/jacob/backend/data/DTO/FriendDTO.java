package com.jacob.backend.data.DTO;

import java.util.UUID;

import com.jacob.backend.data.Model.Status;

public class FriendDTO {

    private String username;

    private Boolean pending;

    private Boolean invitation;

    private UUID currentGameId;

    private Status onlineStatus;

    public FriendDTO(String friendUsername, Boolean isPending, Boolean isInvitation) {
        username = friendUsername;
        pending = isPending;
        invitation = isInvitation;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public Boolean getPending() {
        return pending;
    }

    public void setPending(Boolean pending) {
        this.pending = pending;
    }

    public Boolean getInvitation() {
        return invitation;
    }

    public void setInvitation(Boolean invitation) {
        this.invitation = invitation;
    }

}
