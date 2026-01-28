package com.productservice.dto;

import java.math.BigDecimal;
import java.util.UUID;

public record LikedProductDto(
        UUID id,
        String name,
        String imageUrl,
        BigDecimal price
) {}
