package com.cartservice.dto.request;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

import java.util.UUID;

public record AddItemToCartRequest(
        @NotNull
        UUID productId,

        @NotNull
        @Min(1)
        Integer quantity
) {
}
