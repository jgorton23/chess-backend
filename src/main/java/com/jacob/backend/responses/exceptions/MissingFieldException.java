package com.jacob.backend.responses.exceptions;

public class MissingFieldException extends RuntimeException {
    public MissingFieldException(String form, String field) {
        super(String.format("%s: missing field %s", form, field));
    }
}
