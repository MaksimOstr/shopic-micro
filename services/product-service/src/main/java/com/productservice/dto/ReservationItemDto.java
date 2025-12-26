package com.productservice.dto;

import java.util.UUID;

public record ReservationItemDto(
        UUID id,
        UUID productId,
        int quantity
) {
}
