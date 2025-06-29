package com.orderservice.dto;

import com.orderservice.entity.OrderStatusEnum;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;

@Getter
@Setter
@RequiredArgsConstructor
public class OrderDto {
    private final long id;
    private final OrderStatusEnum status;
    private final BigDecimal price;
    private final Instant updatedAt;
    private final Instant createdAt;
    private final List<OrderItemDto> orderItems;
}
