package com.orderservice.dto;

public record ErrorResponseDto (
        String code,
        int status,
        String message
) {}
