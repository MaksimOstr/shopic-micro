package com.authservice.exceptions;

public class CodeValidationException extends RuntimeException {
    public CodeValidationException(String message) {
        super(message);
    }
}
