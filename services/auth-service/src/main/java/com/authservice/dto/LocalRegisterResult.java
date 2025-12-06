package com.authservice.dto;

import java.time.Instant;
import java.util.UUID;

public record LocalRegisterResult (
        UUID userId,
        String email,
        Instant createdAt
) {}
