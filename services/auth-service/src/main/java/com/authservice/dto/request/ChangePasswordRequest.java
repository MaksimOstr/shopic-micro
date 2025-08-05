package com.authservice.dto.request;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;

public record ChangePasswordRequest (
        @NotBlank
        String oldPassword,

        @NotBlank
        @Min(8)
        String newPassword
) {}
