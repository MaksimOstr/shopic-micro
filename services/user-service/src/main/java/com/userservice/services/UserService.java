package com.userservice.services;

import com.google.common.util.concurrent.AbstractService;
import com.shopic.grpc.userservice.CreateLocalUserRequest;
import com.shopic.grpc.userservice.CreateUserResponse;
import com.shopic.grpc.userservice.UserServiceGrpc;
import com.userservice.dto.CreateProfileDto;
import com.userservice.dto.request.CreateUserRequestDto;
import com.userservice.dto.response.CreateUserResponseDto;
import com.userservice.entity.Profile;
import com.userservice.entity.Role;
import com.userservice.entity.User;
import com.userservice.exceptions.EntityAlreadyExists;
import com.userservice.mapper.UserMapper;
import com.userservice.repositories.UserRepository;
import io.grpc.stub.StreamObserver;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.grpc.server.service.GrpcService;

import java.util.Set;

@Slf4j
@GrpcService
@RequiredArgsConstructor
public class UserService extends UserServiceGrpc.UserServiceImplBase {
    private final ProfileService profileService;
    private final UserRepository userRepository;
    private final UserMapper userMapper;
    private final RoleService roleService;

    @Override
    public void createLocalUser(CreateLocalUserRequest request, StreamObserver<CreateUserResponse> responseObserver) {
        log.info("Auth service received request to create local user:");
        if(isUserExists(request.getEmail())) {
            throw new EntityAlreadyExists("User with such an email already exists");
        }

        Role defaultRole = roleService.getDefaultUserRole();

        User user = new User(
                request.getEmail(),
                request.getPassword(),
                Set.of(defaultRole)
        );
        User savedUser = userRepository.save(user);
        CreateProfileDto profileDto = userMapper.toCreateProfileDto(request.getProfile());
        Profile profile = profileService.createProfile(profileDto , savedUser);
        CreateUserResponse dto = userMapper.toGrpcCreateUserResponse(savedUser, profile);

        responseObserver.onNext(dto);
        responseObserver.onCompleted();


    }

    private boolean isUserExists(String email) {
        return userRepository.existsByEmail(email);
    }
}
