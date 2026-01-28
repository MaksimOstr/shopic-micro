package com.orderservice.dto;

import java.math.BigDecimal;
import java.util.UUID;

public record OrderItemDto (
        UUID id,
        int quantity,
        String name,
        String productImageUrl,
        UUID productId,
        BigDecimal priceAtPurchase
) {}
