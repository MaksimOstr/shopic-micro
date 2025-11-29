package com.authservice.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record LocalRegisterRequest (
        @NotBlank
        @Size(min = 8)
        String password,

        @NotBlank
        @Email
        String email
) {}

