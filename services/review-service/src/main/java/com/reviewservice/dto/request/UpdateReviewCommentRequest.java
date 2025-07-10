package com.reviewservice.dto.request;

import jakarta.validation.constraints.NotBlank;

public record UpdateReviewCommentRequest(
        long reviewId,

        @NotBlank
        String comment
) {}
