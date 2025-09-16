package com.authservice.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

public record ChangeEmailRequest(
        @Email
        @NotBlank
        String newEmail,

        @NotBlank
        String password
) {}
