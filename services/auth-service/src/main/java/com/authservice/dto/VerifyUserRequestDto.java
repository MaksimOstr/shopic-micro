package com.authservice.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record VerifyUserRequestDto(
        @NotBlank(message = "Code should be not blank")
        String code
) {}
