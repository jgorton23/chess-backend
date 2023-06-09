package com.jacob.backend.responses.exceptions;

public class AlreadyFoundException extends RuntimeException {
    public AlreadyFoundException(String type, String identifier) {
        super(String.format("%s with %s already found in database", type, identifier));
    }
}
