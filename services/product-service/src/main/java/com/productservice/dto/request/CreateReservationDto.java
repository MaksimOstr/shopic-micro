package com.productservice.dto.request;

import java.util.List;

public record CreateReservationDto(
        List<ItemForReservation> reservationItems,
        long userId
) {
}
