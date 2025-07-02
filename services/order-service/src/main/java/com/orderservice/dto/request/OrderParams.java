package com.orderservice.dto.request;

import com.orderservice.entity.OrderStatusEnum;

import java.math.BigDecimal;

public record OrderParams(
        OrderStatusEnum status,
        BigDecimal fromPrice,
        BigDecimal toPrice
) {}
