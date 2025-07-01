package com.productservice.projection;

import java.math.BigDecimal;

public record ProductInfoDto (
        long productId,
        BigDecimal price,
        String productImageUrl,
        String productName
) {}
