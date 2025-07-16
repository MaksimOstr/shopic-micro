package com.authservice.services.grpc;

import com.authservice.dto.request.LocalRegisterRequest;
import com.authservice.dto.request.OAuthRegisterRequest;
import com.authservice.exceptions.AlreadyExistsException;
import com.authservice.exceptions.NotFoundException;
import com.shopic.grpc.userservice.*;
import io.grpc.Status;
import io.grpc.StatusRuntimeException;
import lombok.RequiredArgsConstructor;
import org.springframework.grpc.server.service.GrpcService;

import java.util.List;


@GrpcService
@RequiredArgsConstructor
public class UserGrpcService {
    private final UserServiceGrpc.UserServiceBlockingStub userServiceGrpc;

    public UserForAuthGrpcResponse getUserForAuth(String email) {
        try {
            UserForAuthGrpcRequest request = UserForAuthGrpcRequest.newBuilder().setEmail(email).build();
            return userServiceGrpc.getUserForAuth(request);
        } catch (StatusRuntimeException e) {
            System.out.println(e.getStatus());
            System.out.println("Error: " + e.getMessage());
            if(e.getStatus().getCode() == Status.Code.NOT_FOUND) {
                throw new NotFoundException("User with email " + email + " not found");
            }
            throw e;
        }
    }

    public CreateLocalUserGrpcResponse createLocalUser(LocalRegisterRequest dto) {
        ProfileGrpcRequest profile = createGrpcProfile(dto.firstName(), dto.lastName());
        CreateLocalUserGrpcRequest request = CreateLocalUserGrpcRequest.newBuilder()
                .setEmail(dto.email())
                .setPassword(dto.password())
                .setProfile(profile)
                .build();

        try {
            return userServiceGrpc.createLocalUser(request);
        } catch (StatusRuntimeException e) {
            if (e.getStatus().getCode() == Status.Code.ALREADY_EXISTS) {
                throw new AlreadyExistsException("User with email " + request.getEmail() + " already exists");
            }
            throw e;
        }
    }

    public CreateOAuthUserGrpcResponse createOAuthUser(OAuthRegisterRequest dto) {
        ProfileGrpcRequest profile = createGrpcProfile(dto.firstName(), dto.lastName());
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


    private ProfileGrpcRequest createGrpcProfile(String firstName, String lastName) {
        return ProfileGrpcRequest.newBuilder()
                .setLastName(firstName)
                .setFirstName(lastName)
                .build();
    }
}
