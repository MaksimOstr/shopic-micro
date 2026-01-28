package com.orderservice.dto;

import com.orderservice.entity.OrderDeliveryTypeEnum;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;

public record CreateOrderRequest(
        @NotBlank
        String name,

        @NotBlank
        @Pattern(regexp="^\\+?[0-9]{7,15}$")
        String phoneNumber,

        @NotNull
        OrderDeliveryTypeEnum deliveryType,

        @NotBlank
        String address,

        String comment
) {

}
