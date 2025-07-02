package com.orderservice.entity;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.orderservice.exception.NotFoundException;

public enum OrderStatusEnum {
    CREATED,
    PAID,
    PROCESSING,
    SHIPPED,
    DELIVERED,
    COMPLETED,
    CANCELLED,
    FAILED;

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
