package com.paymentservice.entity;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.paymentservice.exception.NotFoundException;

public enum RefundReason {
    DUPLICATE,
    FRAUDULENT,
    REQUESTED_BY_CUSTOMER;

    @JsonCreator
    public static RefundReason fromString(String name) {
        try {
            String uppercaseName = name.toUpperCase();
            return RefundReason.valueOf(uppercaseName);
        } catch (IllegalArgumentException e) {
            throw new NotFoundException(e.getMessage());
        }
    }
}
