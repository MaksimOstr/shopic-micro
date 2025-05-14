package com.userservice.dto.request;

import com.userservice.dto.CreateProfileDto;
import jakarta.validation.Valid;
import jakarta.validation.constraints.*;

public record CreateLocalUserRequestDto (
        @NotBlank
        @Email
        String email,

        @NotBlank
        @Min(8)
        String password,

        @Valid
        CreateProfileDto profile
) {}
