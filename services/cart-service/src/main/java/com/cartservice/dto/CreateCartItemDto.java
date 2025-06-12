package com.cartservice.dto;

public record CreateCartItemDto(
        long productId,
        long cartId,
        int quantity
) {}
