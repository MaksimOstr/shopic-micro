package com.authservice.dto;

import java.time.Instant;

public record UserDto(
        Long id,
        String email,
        Boolean isVerified,
        Instant createdAt,
        Instant updatedAt
) {
}
