package com.jacob.backend.responses;

public class NotFoundException extends RuntimeException {
    public NotFoundException(String type, String identifier) {
        super(String.format("%s with %s not found in database", type, identifier));
    }
}
