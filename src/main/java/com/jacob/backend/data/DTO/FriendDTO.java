package com.jacob.backend.data.DTO;

import javax.json.JsonObject;

import com.jacob.backend.data.JsonConvertible;
import com.jacob.backend.responses.JSONResponses;

public class FriendDTO implements JsonConvertible {

    private String username;

    private Boolean pending;

    private Boolean invitation;

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

    public JsonObject toJson() {
        return JSONResponses.objectBuilder()
                .add("username", username)
                .add("pending", pending)
                .add("invitation", invitation)
                .build();
    }
}
