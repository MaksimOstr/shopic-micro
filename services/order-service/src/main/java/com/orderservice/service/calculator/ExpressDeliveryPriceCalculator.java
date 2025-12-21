package com.orderservice.service.calculator;

import com.orderservice.dto.CreateOrderRequest;
import com.orderservice.entity.OrderDeliveryTypeEnum;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;

@Slf4j
@Component
public class ExpressDeliveryPriceCalculator implements DeliveryPriceCalculator {

    @Override
    public BigDecimal calculateDeliveryPrice(CreateOrderRequest request, BigDecimal totalPrice) {
        log.info("ExpressDeliveryPriceCalculator calculateDeliveryPrice called");
        if(request.deliveryType() != OrderDeliveryTypeEnum.EXPRESS_DELIVERY) {
            throw new IllegalArgumentException("Delivery type must be EXPRESS_DELIVERY");
        }

        return totalPrice.multiply(new BigDecimal("0.03")).add(new BigDecimal("20"));
    }

    @Override
    public OrderDeliveryTypeEnum getDeliveryType() {
        return OrderDeliveryTypeEnum.EXPRESS_DELIVERY;
    }
}
