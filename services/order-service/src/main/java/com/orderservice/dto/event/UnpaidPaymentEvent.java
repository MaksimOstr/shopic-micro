package com.orderservice.dto.event;

public record UnpaidPaymentEvent (
        long orderId
) {}
