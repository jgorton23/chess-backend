package com.jacob.backend.responses;

public class MissingFieldException extends RuntimeException {
    public MissingFieldException(String form, String field) {
        super(String.format("%v: missing field %v", form, field));
    }
}
