package com.authservice.dto.response;

import com.authservice.entity.AuthProviderEnum;

import java.util.List;

public record CreateOAuthUserResponse (
        long userId,
        String email,
        AuthProviderEnum provider,
        List<String> roleNames
) {}
