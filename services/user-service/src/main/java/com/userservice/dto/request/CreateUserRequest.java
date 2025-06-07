package com.userservice.dto.request;

import com.userservice.dto.CreateProfileDto;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public abstract class CreateUserRequest {
    @NotBlank
    @Email
    private final String email;

    @Valid
    private final CreateProfileDto profile;
}
