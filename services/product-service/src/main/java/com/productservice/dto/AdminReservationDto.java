package com.productservice.dto;

import com.productservice.entity.ReservationStatusEnum;

import java.time.Instant;
import java.util.List;

public record AdminReservationDto(
        long id,
        long orderId,
        ReservationStatusEnum status,
        List<AdminReservationItemDto> items,
        Instant updatedAt,
        Instant createdAt
) {
}
