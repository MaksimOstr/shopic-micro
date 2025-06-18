package com.orderservice.dto.request;

import java.math.BigDecimal;

public record CreateOrderItem (
        long productId,
        int quantity,
        BigDecimal price,
        long orderId
) {}
