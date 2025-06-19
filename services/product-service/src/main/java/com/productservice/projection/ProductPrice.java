package com.productservice.projection;

import java.math.BigDecimal;

public record ProductPrice (
        long productId,
        BigDecimal price
) {}
