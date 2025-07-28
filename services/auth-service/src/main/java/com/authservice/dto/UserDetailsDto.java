package com.authservice.dto;

import com.authservice.entity.AuthProviderEnum;

import java.time.Instant;
import java.util.List;

public record UserDetailsDto(
        Long id,
        String email,
        AuthProviderEnum authProvider,
        Boolean isAccountNonLocked,
        Boolean isVerified,
        Instant createdAt,
        List<String> roles
) {}
