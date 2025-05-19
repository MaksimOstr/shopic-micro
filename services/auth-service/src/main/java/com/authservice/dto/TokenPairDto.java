package com.authservice.dto;

public record TokenPairDto (
        String accessToken,
        String refreshToken
) {}
