package com.orderservice.entity;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.orderservice.exception.NotFoundException;

public enum OrderStatusEnum {
    CREATED,
    PAID,
    PROCESSING,
    SHIPPED,
    READY_FOR_PICKUP,
    COMPLETED,
    CANCELLED,
    RETURNED;

    @JsonCreator
    public static OrderStatusEnum fromString(String name) {
        try {
            String uppercaseName = name.toUpperCase();
            return OrderStatusEnum.valueOf(uppercaseName);
        } catch (IllegalArgumentException e) {
            throw new NotFoundException(e.getMessage());
        }
    }
}
