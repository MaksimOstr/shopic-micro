package com.userservice.dto.request;

import com.userservice.dto.CreateProfileDto;
import jakarta.validation.Valid;
import jakarta.validation.constraints.*;

import java.time.LocalDate;

public record CreateUserRequestDto (
        @NotBlank
        @Email
        String email,

        @NotBlank
        @Min(8)
        String password,

        @Valid
        CreateProfileDto profile
) {}
