package com.orderservice.dto.response;

public record ErrorResponseDto (
        String code,
        int status,
        String message
) {}
