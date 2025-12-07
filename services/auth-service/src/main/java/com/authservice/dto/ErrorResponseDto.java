package com.authservice.dto;

public record ErrorResponseDto (
        String code,
        int status,
        String message
) {}
