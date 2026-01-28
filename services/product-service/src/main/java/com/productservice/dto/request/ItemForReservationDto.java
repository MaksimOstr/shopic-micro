package com.productservice.dto.request;

import java.util.UUID;

public record ItemForReservationDto(
        UUID productId,
        int quantity
) {
}
