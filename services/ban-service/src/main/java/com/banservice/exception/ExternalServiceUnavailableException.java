package com.banservice.exception;

public class ExternalServiceUnavailableException extends RuntimeException {
    public ExternalServiceUnavailableException(String message) {
        super(message);
    }
}
