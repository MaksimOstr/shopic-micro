package com.reviewservice.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record CreateReviewReport(
        @NotNull
        Long reviewId,

        @NotBlank
        String description
) {
}
