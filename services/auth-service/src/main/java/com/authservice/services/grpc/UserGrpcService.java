package com.authservice.services.grpc;

import com.authservice.dto.request.LocalRegistrationRequest;
import com.authservice.dto.request.OAuthRegistrationRequest;
import com.authservice.exceptions.EntityAlreadyExistsException;
import com.authservice.exceptions.NotFoundException;
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
                throw new NotFoundException("User with email " + email + " not found");
            }
            throw e;
        }
    }

    public CreateLocalUserResponse createLocalUser(LocalRegistrationRequest dto) {
        String encodedPassword = passwordEncoder.encode(dto.getPassword());
        ProfileRequest profile = ProfileRequest.newBuilder()
                .setLastName(dto.getLastName())
                .setFirstName(dto.getFirstName())
                .build();

        CreateLocalUserRequest request = CreateLocalUserRequest.newBuilder()
                .setEmail(dto.getEmail())
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

    public CreateOAuthUserResponse createGoogleUser(OAuthRegistrationRequest dto) {
        ProfileRequest profile = ProfileRequest.newBuilder()
                .setLastName(dto.getLastName())
                .setFirstName(dto.getFirstName())
                .build();

        CreateOAuthUserRequest request = CreateOAuthUserRequest.newBuilder()
                .setEmail(dto.getEmail())
                .setProfile(profile);
    }


    public List<String> getUserRoleNames(long userId) {
        try {
            UserRolesRequest request = UserRolesRequest.newBuilder().setUserId(userId).build();
            return userServiceGrpc.getUserRoles(request).getRoleNamesList();
        } catch (StatusRuntimeException e) {
            if(e.getStatus().getCode() == Status.Code.NOT_FOUND) {
                throw new NotFoundException("User with id " + userId + " not found");
            }
            throw e;
        }
    }
}
