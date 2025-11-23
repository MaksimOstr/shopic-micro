package com.orderservice.entity;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.orderservice.exception.NotFoundException;

public enum OrderDeliveryTypeEnum {
    SELF_PICKUP,
    EXPRESS_DELIVERY,
    COURIER;

    @JsonCreator
    public static OrderDeliveryTypeEnum fromString(String name) {
        try {
            String uppercaseName = name.toUpperCase();
            return OrderDeliveryTypeEnum.valueOf(uppercaseName);
        } catch (IllegalArgumentException e) {
            throw new NotFoundException("Provided delivery type is not supported");
        }
    }
}
