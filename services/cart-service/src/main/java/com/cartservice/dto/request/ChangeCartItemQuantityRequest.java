package com.cartservice.dto.request;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

public record ChangeCartItemQuantityRequest(
        @NotNull
        @Min(0)
        Integer amount
) {}
