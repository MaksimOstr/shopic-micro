package com.productservice.dto;

public record ErrorResponseDto (
        String code,
        int status,
        String message
) {}
