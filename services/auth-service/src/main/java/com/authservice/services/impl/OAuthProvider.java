package com.authservice.services.impl;

import com.authservice.dto.request.OAuthRegisterRequest;
import com.authservice.dto.response.OAuthRegisterResponse;
import com.authservice.enums.AuthProviderEnum;

public interface OAuthProvider {
    OAuthRegisterResponse handleOAuth(OAuthRegisterRequest request);

    boolean supports(AuthProviderEnum provider);
}
