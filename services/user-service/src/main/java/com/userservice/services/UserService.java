package com.userservice.services;

import com.shopic.grpc.userservice.CreateLocalUserRequest;
import com.shopic.grpc.userservice.CreateUserResponse;
import com.shopic.grpc.userservice.UserServiceGrpc;
import com.userservice.dto.CreateProfileDto;
import com.userservice.entity.Profile;
import com.userservice.entity.Role;
import com.userservice.entity.User;
import com.userservice.exceptions.EntityAlreadyExistsException;
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

    private static final String USER_ALREADY_EXISTS = "User with such an email already exists";

    @Override
    public void createLocalUser(CreateLocalUserRequest request, StreamObserver<CreateUserResponse> responseObserver) {
            log.info("Auth service received request to create local user:");
            if(isUserExist(request.getEmail())) {
                log.error(USER_ALREADY_EXISTS);
                throw new EntityAlreadyExistsException(USER_ALREADY_EXISTS);
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

    private boolean isUserExist(String email) {
        return userRepository.existsByEmail(email);
    }
}
