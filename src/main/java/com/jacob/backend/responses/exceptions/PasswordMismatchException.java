package com.jacob.backend.responses.exceptions;

public class PasswordMismatchException extends RuntimeException {
    public PasswordMismatchException(String pass, String passConfirm) {
        super("Password Confirmation Error: %s does not match %s".formatted(passConfirm, pass));
    }
}
