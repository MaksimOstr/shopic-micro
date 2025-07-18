package com.authservice.exceptions;

public class EmailVerifyException extends RuntimeException {
    public EmailVerifyException(String message) {
        super(message);
    }
}
