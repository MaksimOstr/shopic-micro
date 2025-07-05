package com.paymentservice.dto.event;

public record OrderCanceledEvent(
        long orderId
) {}
