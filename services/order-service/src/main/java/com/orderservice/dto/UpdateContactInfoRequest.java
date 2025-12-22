package com.orderservice.dto;


public record UpdateContactInfoRequest(
        String customerName,
        String address
) {}

