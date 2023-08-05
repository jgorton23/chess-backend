package com.jacob.backend.responses.exceptions;

public class NotFoundException extends RuntimeException {
    public NotFoundException(String type, String identifier) {
        super("%s with %s not found in database".formatted(type, identifier));
    }
}
