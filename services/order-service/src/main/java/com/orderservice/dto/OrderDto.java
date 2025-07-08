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
    private final long orderId;
    private final OrderStatusEnum status;
    private final BigDecimal totalPrice;
    private final Instant updatedAt;
    private final Instant createdAt;
    private final List<OrderItemDto> orderItems;
    private final String country;
    private final String street;
    private final String city;
    private final String postalCode;
    private final String houseNumber;
}
