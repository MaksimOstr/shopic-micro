package com.productservice.dto;

import java.math.BigDecimal;

public record LikedProductDto(
        long id,
        String name,
        String imageUrl,
        BigDecimal price
) {}
