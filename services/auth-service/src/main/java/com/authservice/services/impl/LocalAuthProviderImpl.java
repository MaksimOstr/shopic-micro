package com.authservice.services.impl;

import com.authservice.dto.request.LocalRegisterRequest;
import com.authservice.dto.response.LocalRegisterResponse;
import com.authservice.enums.AuthProviderEnum;
import com.authservice.mapper.AuthMapper;
import com.authservice.services.KafkaEventProducer;
import com.authservice.services.grpc.UserGrpcService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.shopic.grpc.userservice.CreateLocalUserGrpcResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Service;


@Service
@RequiredArgsConstructor
@Primary
public class LocalAuthProviderImpl implements LocalAuthProvider {
    private final UserGrpcService userServiceGrpc;
    private final AuthMapper authMapper;
    private final KafkaEventProducer authEventProducer;


    public LocalRegisterResponse register(LocalRegisterRequest dto) throws JsonProcessingException {
        CreateLocalUserGrpcResponse response = userServiceGrpc.createLocalUser(dto);

        authEventProducer.sendLocalUserCreatedEvent(response.getEmail(), response.getUserId());

        return authMapper.toLocalRegisterResponse(response, AuthProviderEnum.LOCAL);
    };
}
