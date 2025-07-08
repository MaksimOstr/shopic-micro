package com.orderservice.entity;

import jakarta.persistence.Embeddable;
import lombok.*;

@Embeddable
@Builder
@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class OrderCustomer {
    private String firstName;
    private String lastName;
    private String phoneNumber;
}
