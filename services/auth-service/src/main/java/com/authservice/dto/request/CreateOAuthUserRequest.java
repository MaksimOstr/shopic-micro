package com.authservice.dto.request;


import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

public record CreateOAuthUserRequest (
        @NotBlank
        String provider,

        @Email
        String email,

        @NotBlank
        String firstName,

        @NotBlank
        String lastName
) {}
