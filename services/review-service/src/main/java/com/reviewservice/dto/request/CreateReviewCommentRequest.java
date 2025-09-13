package com.reviewservice.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record CreateReviewCommentRequest(
        @NotBlank
        String comment,

        @NotBlank
        String firstName,

        @NotBlank
        String lastName,

        @NotNull
        long reviewId
) {}
