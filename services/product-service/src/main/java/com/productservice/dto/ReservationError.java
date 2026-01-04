package com.productservice.dto;

import com.productservice.enums.ReservationErrorType;

import java.util.UUID;

public record ReservationError(
        UUID productId,
        ReservationErrorType type,
        long requestedQuantity,
        long availableQuantity
) {
}
