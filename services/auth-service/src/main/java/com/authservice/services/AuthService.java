package com.authservice.services;

import com.authservice.dto.request.SignUpRequestDto;
import com.authservice.dto.response.RegisterResponseDto;
import com.authservice.exceptions.RegisterException;
import com.authservice.mapper.AuthMapper;
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
    private final AuthMapper authMaper;
    private final KafkaTemplate<String, String> kafkaTemplate;

    public RegisterResponseDto register(SignUpRequestDto dto) {

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

            kafkaTemplate.send("user-created", "1", "test");

            return authMaper.toRegisterResponseDto(response);
        } catch (StatusRuntimeException e) {
            log.error(e.getStatus().getDescription());
            throw new RegisterException(e.getStatus().getDescription());
        }

    }
}
