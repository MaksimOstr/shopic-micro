package com.authservice.dto;

import jakarta.validation.constraints.NotBlank;

public record VerifyUserRequestDto(
        @NotBlank(message = "Code should be not blank")
        String code
) {}
