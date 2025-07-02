package com.orderservice.dto.request;

import com.orderservice.entity.OrderStatusEnum;

public record AdminOrderParams(
        String firstName,
        String lastName,
        OrderStatusEnum status
) {}
