package com.cartservice.exception;

public class InsufficientProductStockException extends RuntimeException {
    public InsufficientProductStockException(String message) {
        super(message);
    }
}
