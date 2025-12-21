package com.orderservice.dto;

import jakarta.validation.constraints.Pattern;

public record UpdateContactInfoRequest(
        String customerName,
        String address
) {}

