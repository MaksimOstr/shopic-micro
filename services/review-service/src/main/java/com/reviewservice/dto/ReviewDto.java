package com.reviewservice.dto;


import java.math.BigDecimal;
import java.time.Instant;

public record ReviewDto(
        long id,
        long userId,
        BigDecimal rating,
        String comment,
        Instant createdAt,
        Instant updatedAt

) {}
