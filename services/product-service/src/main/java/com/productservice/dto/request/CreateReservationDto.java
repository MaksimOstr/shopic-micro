package com.productservice.dto.request;

public record CreateReservationDto(
        long productId,
        int quantity,
        long userId
) {
}
