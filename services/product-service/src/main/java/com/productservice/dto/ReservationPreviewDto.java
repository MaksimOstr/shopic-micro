package com.productservice.dto;

import com.productservice.entity.ReservationStatusEnum;

import java.time.Instant;
import java.util.UUID;

public record ReservationPreviewDto(
        UUID id,
        UUID orderId,
        ReservationStatusEnum status,
        Instant updatedAt,
        Instant createdAt
) {
}
