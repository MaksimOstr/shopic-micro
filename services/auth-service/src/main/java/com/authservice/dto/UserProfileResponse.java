package com.authservice.dto;

import java.time.Instant;

public record UserProfileResponse(
        Long id,
        String email,
        String firstName,
        String lastName,
        Boolean isVerified,
        Instant createdAt,
        Instant updatedAt
) {
}
