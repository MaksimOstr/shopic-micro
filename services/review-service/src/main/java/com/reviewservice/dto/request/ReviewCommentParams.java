package com.reviewservice.dto.request;

import java.time.Instant;

public record ReviewCommentParams(
        Long userId,
        Long reviewId,
        Instant dateTo,
        Instant dateFrom
) {}
