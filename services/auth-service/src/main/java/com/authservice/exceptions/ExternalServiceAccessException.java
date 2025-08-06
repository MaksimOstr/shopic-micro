package com.authservice.exceptions;

public class ExternalServiceAccessException extends RuntimeException {
    public ExternalServiceAccessException(String message) {
        super(message);
    }
}
