package com.orderservice.dto;

import com.orderservice.entity.OrderStatusEnum;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;

public record OrderDto (
        long id,
        OrderStatusEnum status,
        BigDecimal price,
        Instant updatedAt,
        Instant createdAt,
        List<OrderItemDto> orderItems
) {}
