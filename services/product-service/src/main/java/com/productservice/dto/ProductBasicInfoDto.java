package com.productservice.dto;

import java.math.BigDecimal;

public record ProductBasicInfoDto (
        Long productId,
        BigDecimal price,
        String productImageUrl,
        String productName,
        Integer availableQuantity
) {}
