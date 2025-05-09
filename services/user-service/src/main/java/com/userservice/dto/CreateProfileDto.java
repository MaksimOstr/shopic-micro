package com.userservice.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Past;

import java.time.LocalDate;

public record CreateProfileDto (
        @NotBlank
        String firstName,

        @NotBlank
        String lastName,

        @NotBlank
        String phoneNumber,

        @NotNull
        @Past
        LocalDate birthDate
) {}
