package com.authservice.services;

import com.authservice.dto.event.UserCreatedEvent;
import com.authservice.dto.request.SignUpRequestDto;
import com.authservice.dto.response.RegisterResponseDto;
import com.authservice.exceptions.EntityAlreadyExistsException;
import com.authservice.mapper.AuthMapper;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.shopic.grpc.userservice.CreateLocalUserRequest;
import com.shopic.grpc.userservice.CreateUserResponse;
import com.shopic.grpc.userservice.ProfileRequest;
import com.shopic.grpc.userservice.UserServiceGrpc;
import io.grpc.StatusRuntimeException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class AuthService {
    private final UserServiceGrpc.UserServiceBlockingStub userServiceGrpc;
    private final PasswordEncoder passwordEncoder;
    private final AuthMapper authMapper;
    private final KafkaTemplate<Object, String> kafkaTemplate;
    private final ObjectMapper objectMapper;

    public RegisterResponseDto register(SignUpRequestDto dto) throws JsonProcessingException {

        String encodedPassword = passwordEncoder.encode(dto.password());
        ProfileRequest profile = ProfileRequest.newBuilder()
                .setLastName(dto.lastName())
                .setFirstName(dto.firstName())
                .setPhoneNumber(dto.phoneNumber())
                .build();

        CreateLocalUserRequest req = CreateLocalUserRequest.newBuilder()
                .setEmail(dto.email())
                .setPassword(encodedPassword)
                .setProfile(profile)
                .build();

        try {
            CreateUserResponse response = userServiceGrpc.createLocalUser(req);
            UserCreatedEvent event = new UserCreatedEvent(response.getEmail(), response.getUserId());

            kafkaTemplate.send("user-created", objectMapper.writeValueAsString(event));

            return authMapper.toRegisterResponseDto(response);
        } catch (StatusRuntimeException e) {
            throw new EntityAlreadyExistsException(e.getStatus().getDescription());
        }
    }
}
