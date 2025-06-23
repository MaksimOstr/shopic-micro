package com.userservice.dto.request;

import com.userservice.entity.AuthProviderEnum;

public record UserParams (
        Long id,
        String email,
        Boolean isAccountNonLocked,
        Boolean isVerified,
        AuthProviderEnum provider,
        String firstName,
        String lastName
) {}
