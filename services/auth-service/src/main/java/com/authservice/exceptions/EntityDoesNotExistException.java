package com.authservice.exceptions;

public class EntityDoesNotExistException extends RuntimeException {
    public EntityDoesNotExistException(String message) {
        super(message);
    }
}
