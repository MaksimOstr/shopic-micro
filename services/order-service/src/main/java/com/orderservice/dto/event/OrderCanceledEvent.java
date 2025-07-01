package com.orderservice.dto.event;

public record OrderCanceledEvent(
        long orderId
) {
}
