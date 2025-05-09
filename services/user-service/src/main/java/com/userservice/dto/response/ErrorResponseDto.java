package com.userservice.dto.response;

public record ErrorResponseDto (
        String code,
        int status,
        String message
) {}
