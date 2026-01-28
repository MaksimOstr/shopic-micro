package com.productservice.dto;

import com.productservice.enums.ReservationStatusEnum;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

public record ReservationDto(
        UUID id,
        UUID orderId,
        ReservationStatusEnum status,
        List<ReservationItemDto> items,
        Instant updatedAt,
        Instant createdAt
) {
}
