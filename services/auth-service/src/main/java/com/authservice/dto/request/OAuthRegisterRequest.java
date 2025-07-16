package com.authservice.dto.request;

import com.authservice.entity.AuthProviderEnum;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

public record OAuthRegisterRequest(
        @NotBlank
        @Email
        String email,

        @NotBlank
        String firstName,

        @NotBlank
        String lastName,

        AuthProviderEnum provider
) {}
