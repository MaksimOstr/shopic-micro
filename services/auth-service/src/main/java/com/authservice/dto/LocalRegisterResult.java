package com.authservice.dto;

import java.time.Instant;

public record LocalRegisterResult (
        long userId,
        String email,
        Instant createdAt
) {}
