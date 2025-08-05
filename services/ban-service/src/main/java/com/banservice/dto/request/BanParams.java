package com.banservice.dto.request;

import java.time.Instant;

public record BanParams(
        Long userId,
        Instant bannedFrom,
        Instant bannedTo,
        Long bannerId,
        Long unbannerId,
        Boolean isActive
) {}
