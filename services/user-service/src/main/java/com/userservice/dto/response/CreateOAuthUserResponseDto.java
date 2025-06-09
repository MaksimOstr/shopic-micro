package com.userservice.dto.response;

import com.userservice.entity.AuthProviderEnum;

import java.util.List;

public record CreateOAuthUserResponseDto(
        long userId,
        String email,
        AuthProviderEnum provider,
        List<String> roleNames
) {}
