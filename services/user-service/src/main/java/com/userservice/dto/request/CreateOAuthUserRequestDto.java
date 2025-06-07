package com.userservice.dto.request;

import com.userservice.dto.CreateProfileDto;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
public class CreateOAuthUserRequestDto extends CreateUserRequest {

    private final String provider;

    public CreateOAuthUserRequestDto(String email, String provider, CreateProfileDto profile) {
        super(email, profile);
        this.provider = provider;
    }
}
