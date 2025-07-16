package com.authservice.dto.request;

import com.authservice.dto.CreateProfileDto;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

public record CreateOAuthUserRequest (
        String provider,

        @NotBlank
        @Email
        String email,

        @Valid
        CreateProfileDto profile
) {}
