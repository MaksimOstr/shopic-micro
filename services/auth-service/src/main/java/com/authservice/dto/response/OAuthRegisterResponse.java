package com.authservice.dto.response;

import com.authservice.enums.AuthProviderEnum;
import lombok.Getter;

import java.util.List;


public record OAuthRegisterResponse(
        long userId,
        AuthProviderEnum authProvider,
        List<String> roleNames
) {}
