package com.userservice.exceptions;

public class CodeVerificationException extends RuntimeException {
    public CodeVerificationException(String message) {
        super(message);
    }
}
