package com.userservice.exceptions;

public class ResetPasswordException extends RuntimeException {
    public ResetPasswordException(String message) {
        super(message);
    }
}
