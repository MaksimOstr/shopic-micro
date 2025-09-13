package com.cartservice.dto;

import java.math.BigDecimal;

public record CartItemDto(
        long id,
        long productId,
        int quantity,
        BigDecimal priceAtAdd,
        String productName,
        String productImageUrl
) {}
