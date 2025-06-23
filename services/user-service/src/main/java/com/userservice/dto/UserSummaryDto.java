package com.userservice.dto;

import com.userservice.entity.AuthProviderEnum;

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
