package com.authservice.dto.request;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;

@Getter
public class LocalRegistrationRequest extends BaseAuthRequest {
        @NotBlank
        @Min(8)
        private final String password;

        public LocalRegistrationRequest(String email, String firstName, String lastName, String password) {
                super(email, firstName, lastName);
                this.password = password;
        }
}

