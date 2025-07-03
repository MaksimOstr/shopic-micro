package com.orderservice.dto.event;

public record OrderCompletedEvent(
        long orderId
) {}
