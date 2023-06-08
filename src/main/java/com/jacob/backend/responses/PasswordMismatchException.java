package com.jacob.backend.responses;

public class PasswordMismatchException extends RuntimeException {
    public PasswordMismatchException(String pass, String passConfirm) {
        super(String.format("Password Confirmation Error: %v does not match %v", passConfirm, pass));
    }
}
