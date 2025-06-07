package com.userservice.dto.request;

import com.userservice.dto.CreateProfileDto;
import jakarta.validation.Valid;
import jakarta.validation.constraints.*;


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
