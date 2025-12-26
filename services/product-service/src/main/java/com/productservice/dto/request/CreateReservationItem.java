package com.productservice.dto.request;

import java.util.UUID;

public record CreateReservationItem (
        UUID productId,
        int quantity,
        UUID reservationId
) {}
