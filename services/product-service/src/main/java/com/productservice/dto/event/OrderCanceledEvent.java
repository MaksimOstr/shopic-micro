package com.productservice.dto.event;

public record OrderCanceledEvent(
        long orderId
) {
}
