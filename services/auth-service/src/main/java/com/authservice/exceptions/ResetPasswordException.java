package com.authservice.exceptions;

public class ResetPasswordException extends RuntimeException {
    public ResetPasswordException(String message) {
        super(message);
    }
}
