package com.productservice.exceptions;

public class InvalidEnumArgException extends RuntimeException {
    public InvalidEnumArgException(String message) {
        super(message);
    }
}
