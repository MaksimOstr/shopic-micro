package com.userservice.dto.response;

import java.time.Instant;

public record CreateUserResponseDto (
        long userId,
        String email,
        String firstName,
        String lastName,
        Instant createdAt
) {}
