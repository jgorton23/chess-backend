package com.jacob.backend.responses.exceptions;

public class PasswordMismatchException extends RuntimeException {
    public PasswordMismatchException(String pass, String passConfirm) {
        super(String.format("Password Confirmation Error: %s does not match %s", passConfirm, pass));
    }
}
