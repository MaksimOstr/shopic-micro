package com.orderservice.dto.event;

public record CheckoutSuccessEvent(
        long orderId
) {}
