package com.profileservice.dto;

import java.time.Instant;

public record ProfileDto(
        Long id,
        Long userId,
        String firstName,
        String lastName,
        Instant createdAt,
        Instant updatedAt
) {}
