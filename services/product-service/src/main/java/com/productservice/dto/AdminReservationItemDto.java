package com.productservice.dto;

public record AdminReservationItemDto(
        long id,
        long productId,
        int quantity
) {
}
