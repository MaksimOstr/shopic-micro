package com.orderservice.dto;

import com.orderservice.entity.OrderStatusEnum;
import jakarta.validation.constraints.NotNull;

public record UpdateOrderStatusRequest(
        @NotNull
        OrderStatusEnum targetStatus
) {
}
