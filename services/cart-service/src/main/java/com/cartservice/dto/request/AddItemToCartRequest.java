package com.cartservice.dto.request;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

public record AddItemToCartRequest(
        @NotNull
        Long productId,

        @NotNull
        @Min(1)
        Integer quantity
) {
}
