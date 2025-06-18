package com.productservice.projection;

import java.math.BigDecimal;

public record ProductPriceAndQuantityDto(
        BigDecimal productPrice,
        int stockQuantity
) {}
