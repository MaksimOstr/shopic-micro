package com.cartservice.dto;

public record CreateCartItem(
        long productId,
        long cartId,
        int quantity
) {}
