package com.cartservice.projection;

public record CartItemForOrderProjection (
        long productId,
        String productName,
        String productImageUrl,
        int quantity
) {}
