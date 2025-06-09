package com.userservice.dto.response;

import com.userservice.entity.AuthProviderEnum;

import java.util.List;

public record CreateOAuthUserResponse (
        long userId,
        String email,
        AuthProviderEnum provider,
        List<String> roleNames
) {}
