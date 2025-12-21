package com.orderservice.dto;

import com.orderservice.entity.OrderStatusEnum;

public record AdminOrderParams(
        Long userId,
        String firstName,
        String lastName,
        OrderStatusEnum status
) {}
