package com.reviewservice.dto;

import java.math.BigDecimal;

public record RatingDto(
        long productId,
        BigDecimal rating,
        Integer reviewCount
) {}
