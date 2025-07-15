package com.userservice.dto;

import com.userservice.entity.AuthProviderEnum;

import java.time.Instant;
import java.util.List;
import java.util.Set;

public record UserDetailsDto(
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
