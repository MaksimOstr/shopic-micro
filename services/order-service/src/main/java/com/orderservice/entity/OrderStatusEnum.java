package com.orderservice.entity;


public enum OrderStatusEnum {
    CREATED,
    CONFIRMED,
    PROCESSING,
    SHIPPED,
    READY_FOR_PICKUP,
    COMPLETED,
    CANCELLED,
    RETURNED;
}
