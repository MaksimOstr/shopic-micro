package com.orderservice.dto;

import java.math.BigDecimal;

public record OrderItemDto (
        long id,
        int quantity,
        String name,
        String productImageUrl,
        long productId,
        BigDecimal priceAtPurchase
) {}
