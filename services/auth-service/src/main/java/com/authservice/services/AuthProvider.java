package com.authservice.services;

import com.authservice.dto.request.BaseAuthRequest;
import com.authservice.dto.response.RegisterResponseDto;
import com.authservice.enums.AuthProviderEnum;
import com.fasterxml.jackson.core.JsonProcessingException;

public interface AuthProvider {
    RegisterResponseDto register(BaseAuthRequest dto) throws JsonProcessingException;

    boolean supports(AuthProviderEnum provider);
}
