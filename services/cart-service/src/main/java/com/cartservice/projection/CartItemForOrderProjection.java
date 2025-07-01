package com.cartservice.projection;

public record CartItemForOrderProjection (
        long productId,
        int quantity
) {}
