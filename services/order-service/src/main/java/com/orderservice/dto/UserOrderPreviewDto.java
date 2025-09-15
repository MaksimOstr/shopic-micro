package com.orderservice.dto;

import com.orderservice.entity.OrderStatusEnum;

import java.math.BigDecimal;
import java.time.Instant;

public record UserOrderPreviewDto(
        long orderId,
        OrderStatusEnum status,
        BigDecimal totalPrice,
        Instant createdAt
) {}
