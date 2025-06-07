package com.authservice.services;

import com.authservice.dto.request.BaseAuthRequest;
import com.authservice.dto.request.OAuthRegistrationRequest;
import com.authservice.dto.response.RegisterResponseDto;
import com.authservice.enums.AuthProviderEnum;
import com.authservice.mapper.AuthMapper;
import com.authservice.services.grpc.UserGrpcService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.shopic.grpc.userservice.CreateOAuthUserResponse;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class GoogleAuthProviderImpl implements AuthProvider {
    private final UserGrpcService userGrpcService;
    private final AuthMapper authMapper;
    private final KafkaEventProducer kafkaEventProducer;


    public RegisterResponseDto register(BaseAuthRequest dto) throws JsonProcessingException {
        OAuthRegistrationRequest request = (OAuthRegistrationRequest) dto;
        CreateOAuthUserResponse response = userGrpcService.createGoogleUser(request);

        kafkaEventProducer.sendUserCreatedEvent(response.getEmail(), response.getUserId());

        return authMapper.toRegisterResponseDto(response);
    };

    public boolean supports(AuthProviderEnum provider) {
        return AuthProviderEnum.GOOGLE.equals(provider);
    }
}
