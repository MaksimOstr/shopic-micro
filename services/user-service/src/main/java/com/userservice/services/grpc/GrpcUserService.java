package com.userservice.services.grpc;

import com.shopic.grpc.userservice.*;
import com.userservice.dto.request.CreateLocalUserRequest;
import com.userservice.dto.request.CreateOAuthUserRequest;
import com.userservice.dto.response.CreateOAuthUserResponse;
import com.userservice.dto.response.CreateLocalUserResponse;
import com.userservice.entity.User;
import com.userservice.mapper.GrpcMapper;
import com.userservice.mapper.UserMapper;
import com.userservice.services.LocalUserService;
import com.userservice.services.OAuthUserService;
import com.userservice.services.QueryUserService;
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

    private final GrpcMapper grpcMapper;
    private final QueryUserService queryUserService;
    private final OAuthUserService oAuthUserService;
    private final LocalUserService localUserService;


    @Override
    @Transactional
    public void createLocalUser(CreateLocalUserGrpcRequest request, StreamObserver<CreateLocalUserGrpcResponse> responseObserver) {
        log.info("Auth service received request to create local user: {}", request.toString());

        CreateLocalUserRequest body = grpcMapper.toCreateLocalUserRequest(request);
        CreateLocalUserResponse response = localUserService.createLocalUser(body);
        CreateLocalUserGrpcResponse dto = grpcMapper.toCreateUserGrpcResponse(response);

        log.info("Auth service created local user: {}", dto.toString());

        responseObserver.onNext(dto);
        responseObserver.onCompleted();
    }

    @Override
    public void createOAuthUser(CreateOAuthUserGrpcRequest request, StreamObserver<CreateOAuthUserGrpcResponse> responseObserver) {
        log.info("Auth service received request to create oauth user: {}", request.toString());

        CreateOAuthUserRequest body = grpcMapper.toCreateOAuthUserRequest(request);
        CreateOAuthUserResponse response = oAuthUserService.createOrGetOAuthUser(body);
        CreateOAuthUserGrpcResponse dto = grpcMapper.toCreateOAuthUserGrpcResponse(response);

        responseObserver.onNext(dto);
        responseObserver.onCompleted();
    }


    @Override
    public void getUserForAuth(UserForAuthGrpcRequest request, StreamObserver<UserForAuthGrpcResponse> responseObserver) {
        log.info("Auth service received request to get user for auth: {}", request.toString());

        User user = queryUserService.getUserForAuth(request.getEmail());
        UserForAuthGrpcResponse response = grpcMapper.toAuthResponse(user);

        log.info("Auth service returned user for auth: {}", response.toString());

        responseObserver.onNext(response);
        responseObserver.onCompleted();
    }


    @Override
    public void getUserRoles(UserRolesRequest request, StreamObserver<UserRolesResponse> responseObserver) {
        log.info("Auth service received request to get user roles: {}", request.toString());

        Set<String> roleNames = queryUserService.getUserRoleNames(request.getUserId());

        UserRolesResponse response = UserRolesResponse.newBuilder()
                .addAllRoleNames(roleNames)
                .build();

        responseObserver.onNext(response);
        responseObserver.onCompleted();
    }
}
