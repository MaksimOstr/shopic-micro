package com.orderservice.dto;

import com.orderservice.entity.Address;
import com.orderservice.entity.OrderCustomer;
import com.orderservice.entity.OrderStatusEnum;
import lombok.*;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class AdminOrderDto {
    private long orderId;
    private long userId;
    private OrderStatusEnum status;
    private BigDecimal totalPrice;
    private Instant createdAt;
    private Instant updatedAt;
    private List<OrderItemDto> orderItems;
    private Address address;
    private OrderCustomer customer;
    private String comment;
}
