package com.authservice.dto;

import com.authservice.entity.AuthProviderEnum;

public record UserSummaryDto(
        Long id,
        String email,
        AuthProviderEnum authProvider,
        Boolean isVerified
) {}
