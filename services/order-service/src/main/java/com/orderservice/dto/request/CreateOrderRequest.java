package com.orderservice.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;

public record CreateOrderRequest(
        @NotBlank
        String firstName,

        @NotBlank
        String lastName,

        @NotBlank
        @Pattern(regexp="(^$|[0-9]{10})")
        String phoneNumber,

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
