package com.authservice.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record ChangePasswordRequest (
        @NotBlank(message = "Old password should be not blank")
        String oldPassword,

        @NotBlank(message = "New password should be not blank")
        @Size(min = 8)
        String newPassword
) {}
