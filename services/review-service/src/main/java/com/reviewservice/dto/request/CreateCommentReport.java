package com.reviewservice.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record CreateCommentReport(
        @NotNull
        Long commentId,

        @NotBlank
        String description
) {}
