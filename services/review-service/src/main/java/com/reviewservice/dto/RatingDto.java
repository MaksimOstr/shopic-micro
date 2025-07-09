package com.reviewservice.dto;

import java.math.BigDecimal;

public record RatingDto(
        long productId,
        BigDecimal average,
        int count
) {}
