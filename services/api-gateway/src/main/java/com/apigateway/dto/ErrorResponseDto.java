package com.apigateway.dto;

public record ErrorResponseDto (
        String code,
        int status,
        String message
) {}