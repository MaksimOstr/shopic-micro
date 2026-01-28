package com.orderservice.dto;

import com.orderservice.entity.OrderStatusEnum;

import java.math.BigDecimal;

public record OrderParams(
        OrderStatusEnum status,
        BigDecimal fromPrice,
        BigDecimal toPrice
) {}
