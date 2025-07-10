package com.reviewservice.dto.request;

import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;

import java.math.BigDecimal;

public record UpdateReviewRequest(
        String comment,

        @DecimalMin("1.0")
        @DecimalMax("5.0")
        BigDecimal rating
) {
}
