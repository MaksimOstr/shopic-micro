package com.reviewservice.dto.request;

import java.math.BigDecimal;
import java.time.Instant;

public record ReviewParams(
        Long userId,
        Long productId,
        Instant dateTo,
        Instant dateFrom,
        BigDecimal ratingFrom,
        BigDecimal ratingTo
) {
}
