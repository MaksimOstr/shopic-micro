package com.paymentservice.entity;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.paymentservice.exception.NotFoundException;

public enum RefundReason {
    DUPLICATE,
    FRAUDULENT,
    REQUESTED_BY_CUSTOMER,
    PRODUCT_UNACCEPTABLE,
    PRODUCT_NOT_DELIVERED,
    ORDER_CANCELLATION,
    OTHER;

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
