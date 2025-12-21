package com.orderservice.dto;

import com.orderservice.entity.OrderDeliveryTypeEnum;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;

public record CreateOrderRequest(
        @NotBlank
        String firstName,

        @NotBlank
        String lastName,

        @NotBlank
        @Pattern(regexp="(^$|[0-9]{10})")
        String phoneNumber,

        @NotNull
        OrderDeliveryTypeEnum deliveryType,

        @NotBlank
        String country,

        @NotBlank
        String city,

        @NotBlank
        String postalCode,

        @NotBlank
        String street,

        @NotBlank
        String houseNumber,

        String comment
) {

}
