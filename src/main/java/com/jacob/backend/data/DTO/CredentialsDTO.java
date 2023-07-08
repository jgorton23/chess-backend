package com.jacob.backend.data.DTO;

public class CredentialsDTO {
    private String username;

    private String password;

    private String email;

    private String confirm;

    public CredentialsDTO() {
    }

    public CredentialsDTO(String username, String pass) {
        this.username = username;
        this.password = pass;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String pass) {
        this.password = pass;
    }

    public String getConfirm() {
        return confirm;
    }

    public void setConfirm(String confirm) {
        this.confirm = confirm;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    @Override
    public String toString() {
        return String.join(" ", new String[] { username, password, email, confirm });
    }
}
