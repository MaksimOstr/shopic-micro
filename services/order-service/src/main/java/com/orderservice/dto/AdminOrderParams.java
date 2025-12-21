package com.orderservice.dto;

import com.orderservice.entity.OrderStatusEnum;

import java.util.UUID;

public record AdminOrderParams(
        UUID userId,
        String firstName,
        String lastName,
        OrderStatusEnum status
) {}
