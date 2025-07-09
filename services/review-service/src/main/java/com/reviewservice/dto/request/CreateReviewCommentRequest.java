package com.reviewservice.dto.request;

import jakarta.validation.constraints.NotBlank;

public record CreateReviewCommentRequest(
        @NotBlank
        String comment
) {}
