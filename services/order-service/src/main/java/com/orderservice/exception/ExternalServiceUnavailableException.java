package com.orderservice.exception;

public class ExternalServiceUnavailableException extends RuntimeException {
    public ExternalServiceUnavailableException(String message) {
        super(message);
    }
}
