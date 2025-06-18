package com.productservice.projection;

import java.math.BigDecimal;

public record ProductForCartDto(
        BigDecimal productPrice,
        int stockQuantity
) {}
