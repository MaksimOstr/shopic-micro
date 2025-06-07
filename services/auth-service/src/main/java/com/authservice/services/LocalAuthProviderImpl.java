package com.authservice.services;

import com.authservice.dto.request.BaseAuthRequest;
import com.authservice.dto.request.LocalRegistrationRequest;
import com.authservice.dto.response.RegisterResponseDto;
import com.authservice.enums.AuthProviderEnum;
import com.authservice.mapper.AuthMapper;
import com.authservice.services.grpc.UserGrpcService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.shopic.grpc.userservice.CreateLocalUserResponse;
import com.shopic.grpc.userservice.CreateUserResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class LocalAuthProviderImpl implements AuthProvider {
    private final UserGrpcService userServiceGrpc;
    private final AuthMapper authMapper;
    private final KafkaEventProducer authEventProducer;


    public RegisterResponseDto register(BaseAuthRequest dto) throws JsonProcessingException {
        LocalRegistrationRequest request = (LocalRegistrationRequest) dto;
        CreateLocalUserResponse response = userServiceGrpc.createLocalUser(request);

        authEventProducer.sendUserCreatedEvent(response.getEmail(), response.getUserId());

        return authMapper.toRegisterResponseDto(response);
    };

    public boolean supports(AuthProviderEnum provider) {
        return AuthProviderEnum.LOCAL.equals(provider);
    }
}
