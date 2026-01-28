package com.orderservice.dto;


import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record UpdateContactInfoRequest(
        @NotBlank
        String customerName,

        @NotBlank
        String address
) {}

