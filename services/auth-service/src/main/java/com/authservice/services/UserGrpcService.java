package com.authservice.services;

import com.authservice.dto.request.RegisterRequestDto;
import com.authservice.exceptions.EntityAlreadyExistsException;
import com.authservice.exceptions.EntityDoesNotExistException;
import com.shopic.grpc.userservice.*;
import io.grpc.Status;
import io.grpc.StatusRuntimeException;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;


@Service
@RequiredArgsConstructor
public class UserGrpcService {
    private final UserServiceGrpc.UserServiceBlockingStub userServiceGrpc;
    private final PasswordEncoder passwordEncoder;

    public UserForAuthResponse getUserForAuth(String email) {
        try {
            UserForAuthRequest request = UserForAuthRequest.newBuilder().setEmail(email).build();
            return userServiceGrpc.getUserForAuth(request);
        } catch (StatusRuntimeException e) {
            if(e.getStatus().getCode() == Status.Code.NOT_FOUND) {
                throw new EntityDoesNotExistException("User with email " + email + " not found");
            }
            throw e;
        }
    }

    public CreateUserResponse createUser(RegisterRequestDto dto) {
        String encodedPassword = passwordEncoder.encode(dto.password());
        ProfileRequest profile = ProfileRequest.newBuilder()
                .setLastName(dto.lastName())
                .setFirstName(dto.firstName())
                .setPhoneNumber(dto.phoneNumber())
                .build();

        CreateLocalUserRequest request = CreateLocalUserRequest.newBuilder()
                .setEmail(dto.email())
                .setPassword(encodedPassword)
                .setProfile(profile)
                .build();

        try {
            return userServiceGrpc.createLocalUser(request);
        } catch (StatusRuntimeException e) {
            if (e.getStatus().getCode() == Status.Code.ALREADY_EXISTS) {
                throw new EntityAlreadyExistsException("User with email " + request.getEmail() + " already exists");
            }
            throw e;
        }
    }


    public List<String> getUserRoleNames(long userId) {
        try {
            UserRolesRequest request = UserRolesRequest.newBuilder().setUserId(userId).build();
            return userServiceGrpc.getUserRoles(request).getRoleNamesList();
        } catch (StatusRuntimeException e) {
            Status.Code code = e.getStatus().getCode();
            switch (code) {
                case NOT_FOUND -> throw new EntityDoesNotExistException("User with id " + userId + " not found");
                default -> throw e;
            }
        }
    }
}
