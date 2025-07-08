package com.orderservice.dto;

import com.orderservice.entity.OrderStatusEnum;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;

@Getter
public class AdminOrderDto extends OrderDto {
    private final long userId;

    public AdminOrderDto(
            long orderId,
            OrderStatusEnum status,
            BigDecimal totalPrice,
            Instant updatedAt,
            Instant createdAt,
            List<OrderItemDto> orderItems,
            long userId,
            String country,
            String street,
            String city,
            String postalCode,
            String houseNumber
    ) {
        super(
                orderId,
                status,
                totalPrice,
                updatedAt,
                createdAt,
                orderItems,
                country,
                street,
                city,
                postalCode,
                houseNumber
        );
        this.userId = userId;
    }


}
