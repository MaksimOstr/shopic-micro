package com.cartservice.dto.request;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

public record DecreaseCartItemQuantity(
        @NotNull
        @Min(1)
        Integer amount,

        @NotNull
        Long productId
) {}
