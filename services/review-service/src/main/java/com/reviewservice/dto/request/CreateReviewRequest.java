package com.reviewservice.dto.request;

import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;

public record CreateReviewRequest(
        @NotNull
        Long productId,

        String comment,

        @NotBlank
        String firstName,

        @NotBlank
        String lastName,

        @DecimalMin("1.0")
        @DecimalMax("5.0")
        @NotNull
        BigDecimal rating


) {}
