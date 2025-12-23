package com.orderservice.dto;

import com.orderservice.entity.OrderStatusEnum;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

public record AdminOrderPreviewDto(
        UUID orderId,
        OrderStatusEnum status,
        BigDecimal totalPrice,
        Instant createdAt,
        String customerName
) {}
