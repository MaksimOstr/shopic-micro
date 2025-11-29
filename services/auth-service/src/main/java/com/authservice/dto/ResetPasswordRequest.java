package com.authservice.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record ResetPasswordRequest(
        @NotBlank(message = "New password should be not blank")
        @Size(min = 8)
        String newPassword,

        @NotBlank(message = "Code should be not blank")
        String code
) {}
