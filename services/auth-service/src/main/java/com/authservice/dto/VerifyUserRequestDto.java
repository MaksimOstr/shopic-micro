package com.authservice.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record VerifyUserRequestDto(
        @NotBlank
        @Size(min = 8, max = 8)
        String code
) {}
