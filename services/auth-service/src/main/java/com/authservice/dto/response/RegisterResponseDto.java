package com.authservice.dto.response;

import java.time.Instant;


public record RegisterResponseDto(
        long userId,
        String email,
        String firstName,
        String lastName,
        String phoneNumber,
        Instant cratedAt
) {}
