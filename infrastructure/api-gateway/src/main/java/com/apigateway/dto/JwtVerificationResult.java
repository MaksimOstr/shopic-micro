package com.apigateway.dto;

public record JwtVerificationResult(
        String userId,
        String roles
) {}
