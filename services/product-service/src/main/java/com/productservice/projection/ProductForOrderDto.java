package com.productservice.projection;

import java.math.BigDecimal;

public record ProductForOrderDto (
        long productId,
        BigDecimal price,
        int stockQuantity
) {}
