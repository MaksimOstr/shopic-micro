package com.orderservice.dto.request;

import com.orderservice.entity.OrderStatusEnum;

public record AdminOrderParams(
        long userId,
        String firstName,
        String lastName,
        OrderStatusEnum status
) {}
