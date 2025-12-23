package com.orderservice.dto;

import com.orderservice.entity.OrderStatusEnum;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;
import java.util.UUID;

public record UserOrderDto(
        UUID orderId,
        OrderStatusEnum status,
        BigDecimal totalPrice,
        Instant createdAt,
        Instant updatedAt,
        List<OrderItemDto> orderItems
) {}

