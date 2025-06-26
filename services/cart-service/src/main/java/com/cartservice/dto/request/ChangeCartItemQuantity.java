package com.cartservice.dto.request;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

public record ChangeCartItemQuantity(
        @NotNull
        @Min(0)
        Integer amount,

        @NotNull
        Long cartItemId
) {}
