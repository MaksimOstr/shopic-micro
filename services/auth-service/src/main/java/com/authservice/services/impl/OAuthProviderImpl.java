package com.authservice.services.impl;

import com.authservice.dto.request.OAuthRegisterRequest;
import com.authservice.dto.response.OAuthRegisterResponse;
import com.authservice.enums.AuthProviderEnum;
import com.authservice.mapper.AuthMapper;
import com.authservice.services.OAuthProvider;
import com.authservice.services.grpc.UserGrpcService;
import com.shopic.grpc.userservice.CreateOAuthUserGrpcResponse;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Service;


@Service
@RequiredArgsConstructor
@Primary
public class OAuthProviderImpl implements OAuthProvider {
    private final UserGrpcService userGrpcService;
    private final AuthMapper authMapper;


    public OAuthRegisterResponse register(OAuthRegisterRequest dto) {
        CreateOAuthUserGrpcResponse response = userGrpcService.createOAuthUser(dto);

        return authMapper.toOAuthRegisterResponse(response, AuthProviderEnum.GOOGLE);
    };

   public boolean supports(AuthProviderEnum provider) {
        return provider == AuthProviderEnum.GOOGLE;
    }
}
