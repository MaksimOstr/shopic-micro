package com.orderservice.service.calculator;

import com.orderservice.dto.request.CreateOrderRequest;
import com.orderservice.entity.OrderDeliveryTypeEnum;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;

@Slf4j
@Component
public class CourierDeliveryPriceCalculator implements DeliveryPriceCalculator {
    @Override
    public BigDecimal calculateDeliveryPrice(CreateOrderRequest request, BigDecimal totalPrice) {
        log.info("CourierDeliveryPriceCalculator calculateDeliveryPrice called");
        if(request.deliveryType() != OrderDeliveryTypeEnum.COURIER) {
            throw new IllegalArgumentException("DeliveryType must be COURIER");
        }

        return totalPrice.multiply(new BigDecimal("0.02")).add(new BigDecimal("10"));
    }

    @Override
    public OrderDeliveryTypeEnum getDeliveryType() {
        return OrderDeliveryTypeEnum.COURIER;
    }
}
