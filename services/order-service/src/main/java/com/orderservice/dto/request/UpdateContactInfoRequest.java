package com.orderservice.dto.request;

import jakarta.validation.constraints.Pattern;

public record UpdateContactInfoRequest(
        String firstName,
        String lastName,

        @Pattern(regexp="(^$|[0-9]{10})")
        String phoneNumber,

        String country,
        String street,
        String city,
        String postalCode,
        String houseNumber
) {}

