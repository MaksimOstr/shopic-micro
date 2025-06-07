package com.authservice.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.RequiredArgsConstructor;


@Getter
@RequiredArgsConstructor
public abstract class BaseAuthRequest {
    @NotBlank
    @Email
    private final String email;

    @NotBlank
    private final String firstName;

    @NotBlank
    private final String lastName;

}
