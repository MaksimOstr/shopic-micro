package com.cartservice.dto;

import java.math.BigDecimal;
import java.util.List;

public record CartDto(
        List<CartItemDto> cartItemList,
        BigDecimal totalPrice
) {}
