package com.jacob.backend.responses.exceptions;

public class UnauthorizedException extends RuntimeException {
    public UnauthorizedException() {
        super("UNAUTHORIZED");
    }

    public UnauthorizedException(String innerMessage) {
        super("UNAUTHORIZED: " + innerMessage);
    }
}
