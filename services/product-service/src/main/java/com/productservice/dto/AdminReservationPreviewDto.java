package com.productservice.dto;

import com.productservice.entity.ReservationStatusEnum;

import java.time.Instant;

public record AdminReservationPreviewDto(
        long id,
        long orderId,
        ReservationStatusEnum status,
        Instant updatedAt,
        Instant createdAt
) {
}
