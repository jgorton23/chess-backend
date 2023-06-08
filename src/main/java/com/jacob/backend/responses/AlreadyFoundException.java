package com.jacob.backend.responses;

public class AlreadyFoundException extends RuntimeException {
    public AlreadyFoundException(String type, String identifier) {
        super(String.format("%v with %v already found in database"));
    }
}
