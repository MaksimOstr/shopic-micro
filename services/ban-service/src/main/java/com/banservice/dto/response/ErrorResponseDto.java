package com.banservice.dto.response;

public record ErrorResponseDto (
        String code,
        int status,
        String message
) {}
