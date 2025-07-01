package com.orderservice.dto;

import com.orderservice.entity.OrderStatusEnum;
import lombok.Getter;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;

@Getter
public class AdminOrderDto extends OrderDto {
    private final long userId;

    public AdminOrderDto(long id, OrderStatusEnum status, BigDecimal price, Instant updatedAt, Instant createdAt, List<OrderItemDto> orderItems, long userId) {
        super(id, status, price, updatedAt, createdAt, orderItems);
        this.userId = userId;
    }


}
