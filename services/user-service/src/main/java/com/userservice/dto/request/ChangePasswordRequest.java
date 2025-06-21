package com.userservice.dto.request;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;

public record ChangePasswordRequest(
        @NotBlank
        @Min(8)
        String newPassword
) {}
