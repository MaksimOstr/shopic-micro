package com.authservice.services;

import com.authservice.dto.request.OAuthRegisterRequest;
import com.authservice.dto.response.OAuthRegisterResponse;
import com.authservice.enums.AuthProviderEnum;
import com.fasterxml.jackson.core.JsonProcessingException;

public interface OAuthProvider {
    OAuthRegisterResponse register(OAuthRegisterRequest request);

    boolean supports(AuthProviderEnum provider);
}
