package com.orderservice.entity;

import jakarta.persistence.Embeddable;

@Embeddable
public record OrderCustomer(
        String firstName,
        String lastName,
        String phoneNumber
) {
}
