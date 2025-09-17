package com.authservice.dto.request;


import com.authservice.entity.AuthProviderEnum;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record CreateOAuthUserRequest (
        @NotNull
        AuthProviderEnum provider,

        @Email
        String email,

        @NotBlank
        String firstName,

        @NotBlank
        String lastName
) {}
