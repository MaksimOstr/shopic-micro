package com.authservice.dto;

import com.authservice.entity.AuthProviderEnum;

import java.time.Instant;
import java.util.List;

public record UserDetailsDto(
        Long id,
        String email,
        AuthProviderEnum authProvider,
        String firstName,
        String lastName,
        Boolean isVerified,
        Boolean isNonBlocked,
        Instant createdAt,
        Instant updatedAt,
        List<String> roles
) {}
