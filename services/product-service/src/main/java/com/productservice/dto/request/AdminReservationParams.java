package com.productservice.dto.request;

import com.productservice.enums.ReservationStatusEnum;

import java.util.UUID;

public record AdminReservationParams(
        ReservationStatusEnum status,
        String orderId
) {
}
