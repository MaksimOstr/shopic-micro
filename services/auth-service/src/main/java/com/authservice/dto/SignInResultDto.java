package com.authservice.dto;

public record SignInResultDto(
        String refreshToken,
        String accessToken
) {}
