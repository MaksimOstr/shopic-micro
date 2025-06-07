package com.authservice.services.grpc;

import com.authservice.dto.request.LocalRegisterRequest;
import com.authservice.dto.request.OAuthRegisterRequest;
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

    public UserForAuthGrpcResponse getUserForAuth(String email) {
        try {
            UserForAuthGrpcRequest request = UserForAuthGrpcRequest.newBuilder().setEmail(email).build();
            return userServiceGrpc.getUserForAuth(request);
        } catch (StatusRuntimeException e) {
            if(e.getStatus().getCode() == Status.Code.NOT_FOUND) {
                throw new NotFoundException("User with email " + email + " not found");
            }
            throw e;
        }
    }

    public CreateLocalUserGrpcResponse createLocalUser(LocalRegisterRequest dto) {
        String encodedPassword = passwordEncoder.encode(dto.password());
        ProfileRequest profile = ProfileRequest.newBuilder()
                .setLastName(dto.lastName())
                .setFirstName(dto.firstName())
                .build();

        CreateLocalUserGrpcRequest request = CreateLocalUserGrpcRequest.newBuilder()
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


    public CreateOAuthUserGrpcResponse createOAuthUser(OAuthRegisterRequest dto) {
        ProfileRequest profile = ProfileRequest.newBuilder()
                .setLastName(dto.lastName())
                .setFirstName(dto.firstName())
                .build();

        CreateOAuthUserGrpcRequest request = CreateOAuthUserGrpcRequest.newBuilder()
                .setEmail(dto.email())
                .setProvider(dto.provider().name())
                .setProfile(profile)
                .build();

        return userServiceGrpc.createOAuthUser(request);
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
