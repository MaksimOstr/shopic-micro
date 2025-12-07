package com.authservice.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

public record ForgotPasswordRequest(
        @NotBlank(message = "Email should be valid and not blank")
        @Email(message = "Email should be valid")
        String email
) {
}
