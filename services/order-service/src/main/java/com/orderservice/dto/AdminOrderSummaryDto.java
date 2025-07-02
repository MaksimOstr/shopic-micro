package com.orderservice.dto;

import com.orderservice.entity.OrderStatusEnum;

import java.math.BigDecimal;
import java.time.Instant;

public record AdminOrderSummaryDto(
        Long orderId,
        OrderStatusEnum status,
        BigDecimal totalPrice,
        Instant createdAt,
        String firstName,
        String lastName
) {}
