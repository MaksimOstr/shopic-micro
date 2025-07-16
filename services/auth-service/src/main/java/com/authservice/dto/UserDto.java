package com.authservice.dto;

import com.authservice.entity.AuthProviderEnum;

import java.time.Instant;
import java.util.List;

public record UserDto(
        Long id,
        String email,
        String firstName,
        String lastName,
        AuthProviderEnum authProvider,
        Boolean isAccountNonLocked,
        Boolean isVerified,
        Instant createdAt,
        String phoneNumber,
        List<String> roles
) {}
