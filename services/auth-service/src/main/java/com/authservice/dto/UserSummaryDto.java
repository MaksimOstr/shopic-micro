package com.authservice.dto;

import com.authservice.entity.AuthProviderEnum;

import java.time.Instant;

public record UserSummaryDto(
        Long id,
        String email,
        String firstName,
        String lastName,
        AuthProviderEnum authProvider,
        Boolean isAccountNonLocked,
        Boolean isVerified,
        Instant createdAt
) {}
