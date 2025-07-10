package com.reviewservice.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record CreateReviewCommentRequest(
        @NotBlank
        String comment,

        @NotNull
        long reviewId
) {}
