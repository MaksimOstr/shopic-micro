package com.productservice.dto.request;

public record ItemForReservationDto(
        long productId,
        int quantity
) {
}
