package com.orderservice.dto;

import com.orderservice.entity.OrderStatusEnum;

import java.math.BigDecimal;
import java.util.List;

public record OrderDto (
        long id,
        OrderStatusEnum status,
        BigDecimal price,
        List<OrderItemDto> orderItems
) {}
