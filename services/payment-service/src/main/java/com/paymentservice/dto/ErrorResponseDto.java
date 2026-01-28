package com.paymentservice.dto;

public record ErrorResponseDto(
        String code,
        int status,
        String message
) {}
