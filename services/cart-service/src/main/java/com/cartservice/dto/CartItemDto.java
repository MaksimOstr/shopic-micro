package com.cartservice.dto;

import java.math.BigDecimal;
import java.util.UUID;

public record CartItemDto(
        UUID id,
        long productId,
        int quantity,
        BigDecimal priceAtAdd,
        String productName,
        String productImageUrl
) {}
