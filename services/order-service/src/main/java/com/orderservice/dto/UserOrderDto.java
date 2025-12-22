package com.orderservice.dto;

import com.orderservice.entity.OrderStatusEnum;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;

public record UserOrderDto(
        long orderId,
        OrderStatusEnum status,
        BigDecimal totalPrice,
        Instant createdAt,
        Instant updatedAt,
        List<OrderItemDto> orderItems
) {}

