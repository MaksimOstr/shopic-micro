package com.cartservice.dto;

import java.util.UUID;

public record CartItemDtoForOrder (
        UUID productId,
        int quantity
) {}
