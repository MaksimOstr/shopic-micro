package com.authservice.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record LocalRegisterRequest (
        @NotBlank(message = "Password should be not blank")
        @Size(min = 8)
        String password,

        @NotBlank(message = "Confirm password should be not blank")
        String confirmPassword,

        @NotBlank(message = "Email should be valid and not blank")
        @Email(message = "Email should be valid")
        String email
) {}

