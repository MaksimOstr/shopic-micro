package com.orderservice.service.calculator;

import com.orderservice.dto.CreateOrderRequest;
import com.orderservice.entity.OrderDeliveryTypeEnum;

import java.math.BigDecimal;

public interface DeliveryPriceCalculator {
    BigDecimal calculateDeliveryPrice(CreateOrderRequest request, BigDecimal totalPrice);

    OrderDeliveryTypeEnum getDeliveryType();
}
