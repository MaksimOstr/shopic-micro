package com.userservice.dto.request;

import java.time.Instant;

public record BanParams(
        Long userId,
        Instant bannedFrom,
        Instant bannedTo,
        Long bannedBy,
        Long unbannedBy,
        Boolean isActive
) {}
