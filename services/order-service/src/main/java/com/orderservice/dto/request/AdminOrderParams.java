package com.orderservice.dto.request;

import com.orderservice.entity.OrderStatusEnum;

public record AdminOrderParams(
        Long userId,
        String firstName,
        String lastName,
        OrderStatusEnum status
) {}
