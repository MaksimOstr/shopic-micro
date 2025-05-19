package com.userservice.services;

import com.shopic.grpc.userservice.*;
import com.userservice.dto.request.CreateLocalUserRequestDto;
import com.userservice.dto.response.CreateUserResponseDto;
import com.userservice.entity.Role;
import com.userservice.entity.User;
import com.userservice.mapper.UserMapper;
import io.grpc.stub.StreamObserver;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.grpc.server.service.GrpcService;


@Slf4j
@GrpcService
@RequiredArgsConstructor
public class GrpcUserService extends UserServiceGrpc.UserServiceImplBase {

    private final UserMapper userMapper;
    private final UserService userService;



    @Override
    @Transactional
    public void createLocalUser(CreateLocalUserRequest request, StreamObserver<CreateUserResponse> responseObserver) {
        log.info("Auth service received request to create local user: {}", request.toString());

        CreateLocalUserRequestDto body = userMapper.toCreateLocalUserRequestDto(request);
        CreateUserResponseDto response = userService.createLocalUser(body);
        CreateUserResponse dto = userMapper.toGrpcCreateUserResponse(response);

        log.info("Auth service created local user: {}", dto.toString());

        responseObserver.onNext(dto);
        responseObserver.onCompleted();
    }

    @Override
    public void getUserForAuth(UserForAuthRequest request, StreamObserver<UserForAuthResponse> responseObserver) {
        log.info("Auth service received request to get user for auth: {}", request.toString());

        User user = userService.getUserForAuth(request.getEmail());
        System.out.println("3333333333333333333333333333333333");
        System.out.println(user.toString());


        UserForAuthResponse response = userMapper.toAuthResponse(user);

        log.info("Auth service returned user for auth: {}", response.toString());

        responseObserver.onNext(response);
        responseObserver.onCompleted();
    }
}
