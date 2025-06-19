package com.cartservice.projection;

import java.math.BigDecimal;

public record CartItemProjection(
        long id,
        long productId,
        int quantity,
        BigDecimal priceAtAdd,
        String productName,
        String productImageUrl
) {}
