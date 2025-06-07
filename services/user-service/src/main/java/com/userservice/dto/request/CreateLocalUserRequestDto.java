package com.userservice.dto.request;

import com.userservice.dto.CreateProfileDto;
import jakarta.validation.constraints.*;
import lombok.Getter;

@Getter
public class CreateLocalUserRequestDto extends CreateUserRequest {
        @NotBlank
        @Min(8)
        String password;

        public CreateLocalUserRequestDto(String email, CreateProfileDto profile) {
                super(email, profile);
        }
}
