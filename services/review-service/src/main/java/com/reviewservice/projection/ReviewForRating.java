package com.reviewservice.projection;

import java.math.BigDecimal;

public record ReviewForRating(
        long productId,
        BigDecimal rating
) {}
