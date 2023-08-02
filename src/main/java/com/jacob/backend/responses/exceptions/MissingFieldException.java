package com.jacob.backend.responses.exceptions;

public class MissingFieldException extends RuntimeException {
    public MissingFieldException(String form, String field) {
        super("%s: missing field %s".formatted(form, field));
    }
}
