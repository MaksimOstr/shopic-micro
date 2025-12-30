package com.productservice.dto;


import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;


public record AdminProductDto(
        UUID id,
        String productName,
        String description,
        String imageUrl,
        int stockQuantity,
        BigDecimal price,
        UUID categoryId,
        UUID brandId,
        String categoryName,
        String brandName,
        Boolean deleted,
        Instant createdAt
) {}