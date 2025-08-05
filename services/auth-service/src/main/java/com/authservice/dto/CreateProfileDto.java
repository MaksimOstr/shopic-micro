package com.authservice.dto;

import jakarta.validation.constraints.NotBlank;

public record CreateProfileDto (
        @NotBlank
        String firstName,

        @NotBlank
        String lastName
) {}
