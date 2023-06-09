package com.jacob.backend.responses;

public class InvalidCredentialsException extends RuntimeException {
    public InvalidCredentialsException(String field) {
        super(String.format("Invalid %s", field));
    }
}
