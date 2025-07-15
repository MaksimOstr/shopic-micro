package com.userservice.dto;

import java.time.Instant;

public record BanDto(
        long id,
        long userId,
        Instant banTo,
        long bannedBy,
        String reason,
        long unbannedBy,
        boolean isActive,
        Instant createdAt
        ) {}
