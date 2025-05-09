package com.authservice.services;

import com.authservice.dto.request.SignUpRequestDto;
import com.shopic.grpc.userservice.CreateLocalUserRequest;
import com.shopic.grpc.userservice.CreateUserResponse;
import com.shopic.grpc.userservice.ProfileRequest;
import com.shopic.grpc.userservice.UserServiceGrpc;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthService {
    private final UserServiceGrpc.UserServiceBlockingStub userServiceGrpc;
    private final PasswordEncoder passwordEncoder;

    public void register(SignUpRequestDto dto) {

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

        CreateUserResponse test = userServiceGrpc.createLocalUser(req);
        System.out.println(test.toString());
    }
}
