package com.reviewservice.dto.request;

import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;

public record CreateReviewRequest(
        @NotNull
        Long productId,

        String comment,

        @DecimalMin("1.0")
        @DecimalMax("5.0")
        @NotNull
        BigDecimal rating


) {}
