package com.paymentservice.dto.event;

public record CheckoutSuccessEvent(
        long orderId
) {
}
