package com.authservice.dto;

import java.time.Instant;
import java.util.UUID;

public record UserDto(
        UUID id,
        String email,
        Boolean isVerified,
        Instant createdAt,
        Instant updatedAt
) {
}
