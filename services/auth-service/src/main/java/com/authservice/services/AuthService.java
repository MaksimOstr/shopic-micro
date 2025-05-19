package com.authservice.services;

import com.authservice.config.security.model.CustomUserDetails;
import com.authservice.dto.TokenPairDto;
import com.authservice.dto.event.UserCreatedEvent;
import com.authservice.dto.request.SignInRequestDto;
import com.authservice.dto.request.RegisterRequestDto;
import com.authservice.dto.response.RegisterResponseDto;
import com.authservice.exceptions.EntityAlreadyExistsException;
import com.authservice.mapper.AuthMapper;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.shopic.grpc.userservice.CreateLocalUserRequest;
import com.shopic.grpc.userservice.CreateUserResponse;
import com.shopic.grpc.userservice.ProfileRequest;
import com.shopic.grpc.userservice.UserServiceGrpc;
import io.grpc.Status;
import io.grpc.StatusRuntimeException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Objects;
import java.util.Set;

import static com.authservice.utils.AuthUtils.mapUserRoles;

@Slf4j
@Service
@RequiredArgsConstructor
public class AuthService {
    private final UserServiceGrpc.UserServiceBlockingStub userServiceGrpc;
    private final PasswordEncoder passwordEncoder;
    private final AuthMapper authMapper;
    private final KafkaTemplate<Object, String> kafkaTemplate;
    private final ObjectMapper objectMapper;
    private final AuthenticationManager authenticationManager;
    private final TokenService tokenService;

    public RegisterResponseDto register(RegisterRequestDto dto) throws JsonProcessingException {
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
            if (Objects.requireNonNull(e.getStatus().getCode()) == Status.Code.ALREADY_EXISTS) {
                throw new EntityAlreadyExistsException("User with email " + dto.email() + " already exists");
            }
            throw e;
        }
    }

    public TokenPairDto signIn(SignInRequestDto dto) {
        Authentication authReq = new UsernamePasswordAuthenticationToken(dto.email(), dto.password());
        Authentication authenticatedUser = authenticationManager.authenticate(authReq);
        CustomUserDetails customUserDetails = (CustomUserDetails) authenticatedUser.getPrincipal();

        long userId = customUserDetails.getUserId();
        Set<String> roles = mapUserRoles(customUserDetails.getAuthorities());

        return tokenService.getTokenPair(userId, roles, dto.deviceId());
    }
}
