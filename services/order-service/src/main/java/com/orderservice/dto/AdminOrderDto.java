package com.orderservice.dto;

import com.orderservice.entity.OrderStatusEnum;
import lombok.*;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;
import java.util.UUID;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class AdminOrderDto {
    private UUID orderId;
    private UUID userId;
    private String customerName;
    private String address;
    private OrderStatusEnum status;
    private BigDecimal totalPrice;
    private Instant createdAt;
    private Instant updatedAt;
    private List<OrderItemDto> orderItems;
    private String comment;
}
