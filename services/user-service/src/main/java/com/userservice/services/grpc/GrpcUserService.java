package com.userservice.services.grpc;

import com.shopic.grpc.userservice.*;
import com.userservice.dto.request.CreateLocalUserRequestDto;
import com.userservice.dto.request.CreateOAuthUserRequestDto;
import com.userservice.dto.response.CreateOAuthUserResponseDto;
import com.userservice.dto.response.CreateUserResponseDto;
import com.userservice.entity.User;
import com.userservice.mapper.UserMapper;
import com.userservice.services.UserService;
import io.grpc.stub.StreamObserver;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.grpc.server.service.GrpcService;

import java.util.Set;


@Slf4j
@GrpcService
@RequiredArgsConstructor
public class GrpcUserService extends UserServiceGrpc.UserServiceImplBase {

    private final UserMapper userMapper;
    private final UserService userService;


    @Override
    @Transactional
    public void createLocalUser(CreateLocalUserRequest request, StreamObserver<CreateLocalUserResponse> responseObserver) {
        log.info("Auth service received request to create local user: {}", request.toString());

        CreateLocalUserRequestDto body = userMapper.toCreateLocalUserRequestDto(request);
        CreateUserResponseDto response = userService.createLocalUser(body);
        CreateLocalUserResponse dto = userMapper.toGrpcCreateUserResponse(response);

        log.info("Auth service created local user: {}", dto.toString());

        responseObserver.onNext(dto);
        responseObserver.onCompleted();
    }

    @Override
    public void createOAuthUser(CreateOAuthUserRequest request, StreamObserver<CreateOAuthUserResponse> responseObserver) {
        log.info("Auth service received request to create oauth user: {}", request.toString());

        CreateOAuthUserRequestDto body = userMapper.toCreateOAuthUserRequestDto(request);

        CreateOAuthUserResponseDto response = userService.createOAuthUser(body);

        CreateOAuthUserResponse dto = userMapper.toCreateOAuthUserResponseDto(response);

        responseObserver.onNext(dto);
        responseObserver.onCompleted();
    }


    @Override
    public void getUserForAuth(UserForAuthRequest request, StreamObserver<UserForAuthResponse> responseObserver) {
        log.info("Auth service received request to get user for auth: {}", request.toString());

        User user = userService.getUserForAuth(request.getEmail());

        UserForAuthResponse response = userMapper.toAuthResponse(user);

        log.info("Auth service returned user for auth: {}", response.toString());

        responseObserver.onNext(response);
        responseObserver.onCompleted();
    }


    @Override
    public void getUserRoles(UserRolesRequest request, StreamObserver<UserRolesResponse> responseObserver) {
        log.info("Auth service received request to get user roles: {}", request.toString());

        Set<String> roleNames = userService.getUserRoleNames(request.getUserId());

        UserRolesResponse response = UserRolesResponse.newBuilder()
                .addAllRoleNames(roleNames)
                .build();

        responseObserver.onNext(response);
        responseObserver.onCompleted();
    }
}
