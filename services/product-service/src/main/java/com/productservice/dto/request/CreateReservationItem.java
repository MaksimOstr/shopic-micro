package com.productservice.dto.request;

public record CreateReservationItem (
        long productId,
        int quantity,
        long reservationId
) {}
