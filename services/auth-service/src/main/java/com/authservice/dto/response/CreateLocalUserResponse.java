package com.authservice.dto.response;

import java.time.Instant;

public record CreateLocalUserResponse (
        long userId,
        String email,
        Instant createdAt
) {}
