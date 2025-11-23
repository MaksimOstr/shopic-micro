package com.orderservice.service.calculator;

import com.orderservice.dto.request.CreateOrderRequest;
import com.orderservice.entity.OrderDeliveryTypeEnum;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;

@Slf4j
@Component
public class SelfPickupDeliveryPriceCalculator implements DeliveryPriceCalculator {
    @Override
    public BigDecimal calculateDeliveryPrice(CreateOrderRequest request, BigDecimal totalPrice) {
        log.info("SelfPickupDeliveryPriceCalculator calculateDeliveryPrice called");
        if(request.deliveryType() != OrderDeliveryTypeEnum.SELF_PICKUP) {
            throw new IllegalArgumentException("Delivery type must be SELF_PICKUP");
        }

        return BigDecimal.ZERO;
    }

    @Override
    public OrderDeliveryTypeEnum getDeliveryType() {
        return OrderDeliveryTypeEnum.SELF_PICKUP;
    }
}
