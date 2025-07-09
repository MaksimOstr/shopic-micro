package com.reviewservice.dto;

import java.time.Instant;

public record ReviewCommentDto(
        long id,
        long userId,
        long reviewId,
        String comment,
        Instant createdAt
) {}
