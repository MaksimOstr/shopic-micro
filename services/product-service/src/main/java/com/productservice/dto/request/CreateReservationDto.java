package com.productservice.dto.request;

import java.util.List;
import java.util.UUID;

public record CreateReservationDto(
        List<ItemForReservationDto> reservationItems,
        UUID orderId
) {
}
