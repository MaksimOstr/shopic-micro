package com.orderservice.entity;


import com.fasterxml.jackson.annotation.JsonCreator;
import com.orderservice.exception.ApiException;
import org.springframework.http.HttpStatus;


public enum OrderStatusEnum {
    PENDING,
    PROCESSING,
    SHIPPED,
    COMPLETED,
    CANCELLED;

    @JsonCreator
    public static OrderStatusEnum fromString(String value) {
        try {
            return OrderStatusEnum.valueOf(value.toUpperCase());
        } catch (IllegalArgumentException ex) {
            throw new ApiException("Invalid order status value: " + value, HttpStatus.BAD_REQUEST);
        }
    }
}
