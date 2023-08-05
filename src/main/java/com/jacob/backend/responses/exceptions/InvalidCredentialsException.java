package com.jacob.backend.responses.exceptions;

public class InvalidCredentialsException extends RuntimeException {
    public InvalidCredentialsException(String field) {
        super("Invalid %s".formatted(field));
    }
}
