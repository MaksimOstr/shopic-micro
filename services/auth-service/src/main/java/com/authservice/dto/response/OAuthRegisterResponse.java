package com.authservice.dto.response;

import com.authservice.enums.AuthProviderEnum;

import java.util.Set;


public record OAuthRegisterResponse(
        long userId,
        AuthProviderEnum authProvider,
        Set<String> roleNames
) {}
