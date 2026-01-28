package com.productservice.dto.event;

import java.util.UUID;

public record BasePaymentEvent(
        UUID paymentId,
        UUID orderId
) {
}
