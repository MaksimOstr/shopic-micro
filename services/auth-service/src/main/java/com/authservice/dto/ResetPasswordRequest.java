package com.authservice.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;

public record ResetPasswordRequest(
        @NotBlank
        @Min(8)
        String newPassword,

        @NotBlank
        String code
) {}
