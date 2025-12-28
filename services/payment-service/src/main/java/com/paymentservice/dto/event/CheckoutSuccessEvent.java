package com.paymentservice.dto.event;

import java.util.UUID;

public record CheckoutSuccessEvent(
        UUID orderId
) {
}
