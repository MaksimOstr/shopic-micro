package com.paymentservice.dto;

import java.math.BigDecimal;

public record CheckoutItem(
        String imageUrl,
        String name,
        long quantity,
        BigDecimal price
) {}
