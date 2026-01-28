package com.orderservice.dto.event;

import java.util.UUID;

public record ReservationCancelledEvent(
        UUID orderId,
        UUID reservationId
) {
}
