package com.authservice.dto.request;

import com.authservice.dto.CreateProfileDto;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;

public record CreateLocalUserRequest (
        @NotBlank
        @Min(8)
        String password,

        @NotBlank
        @Email
        String email,

        @Valid
        CreateProfileDto profile
) {
}
