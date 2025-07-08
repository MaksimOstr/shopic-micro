package com.orderservice.entity;

import jakarta.persistence.Embeddable;
import lombok.*;

@Embeddable
@Builder
@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class Address {
    private String country;
    private String street;
    private String city;
    private String postalCode;
    private String houseNumber;
}
