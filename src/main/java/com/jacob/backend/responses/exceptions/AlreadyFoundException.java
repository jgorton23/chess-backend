package com.jacob.backend.responses.exceptions;

public class AlreadyFoundException extends RuntimeException {
    public AlreadyFoundException(String type, String identifier) {
        super("%s with %s already found in database".formatted(type, identifier));
    }
}
