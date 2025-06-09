package com.productservice.dto.request;

import com.productservice.entity.Brand;

import java.math.BigDecimal;

public record ToggleLikeRequest (
        long productId,
        String name,
        String description,
        String sku,
        BigDecimal price,
        String brandName,
        String categoryName,
        int stockQuantity,
        boolean isLiked
) {}
