package com.paymentservice.exception;

public class RefundRequestException extends RuntimeException {
    public RefundRequestException(String message) {
        super(message);
    }
}
