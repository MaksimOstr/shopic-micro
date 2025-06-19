package com.productservice.dto.request;

public record ItemForReservation(
        long productId,
        int quantity
) {
}
