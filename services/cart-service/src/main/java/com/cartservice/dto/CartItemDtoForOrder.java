package com.cartservice.dto;

public record CartItemDtoForOrder (
        long productId,
        int quantity
) {}
