package com.authservice.dto.request;

import com.authservice.entity.AuthProviderEnum;

public record UserParams (
        Long id,
        String email,
        Boolean isAccountNonLocked,
        Boolean isVerified,
        AuthProviderEnum provider
) {}
