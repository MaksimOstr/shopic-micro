package com.banservice.dto;

import java.time.Instant;

public record BanDto(
        long id,
        long userId,
        Instant banTo,
        long bannerId,
        String reason,
        long unbannerId,
        boolean isActive,
        Instant createdAt
) {}
