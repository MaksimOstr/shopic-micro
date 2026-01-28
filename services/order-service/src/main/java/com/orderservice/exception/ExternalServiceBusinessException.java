package com.orderservice.exception;

import org.springframework.http.HttpStatus;

public class ExternalServiceBusinessException extends ApiException {
    public ExternalServiceBusinessException(String message, HttpStatus status) {
        super(message, status);
    }
}
