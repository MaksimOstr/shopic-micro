package com.userservice.services;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.shopic.grpc.codeservice.CodeScopeEnum;
import com.shopic.grpc.codeservice.CodeServiceGrpc;
import com.shopic.grpc.codeservice.ValidateCodeRequest;
import com.shopic.grpc.codeservice.ValidateCodeResponse;
import com.userservice.dto.event.EmailVerifyRequestDto;
import com.userservice.dto.request.CreateLocalUserRequestDto;
import com.userservice.dto.response.CreateUserResponseDto;
import com.userservice.entity.Profile;
import com.userservice.entity.Role;
import com.userservice.entity.User;
import com.userservice.exceptions.CodeVerificationException;
import com.userservice.exceptions.EntityAlreadyExistsException;
import com.userservice.exceptions.EntityDoesNotExistException;
import com.userservice.mapper.UserMapper;
import com.userservice.repositories.UserRepository;
import io.grpc.Status;
import io.grpc.StatusRuntimeException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import java.util.Set;


@Slf4j
@Service
@RequiredArgsConstructor
public class UserService {
    private final UserRepository userRepository;
    private final ProfileService profileService;
    private final RoleService roleService;
    private final UserMapper userMapper;


    private static final String USER_ALREADY_EXISTS = "User with such an email already exists";


    public CreateUserResponseDto createLocalUser(CreateLocalUserRequestDto dto) {
        if(isUserExist(dto.email())) {
            log.error(USER_ALREADY_EXISTS);
            throw new EntityAlreadyExistsException(USER_ALREADY_EXISTS);
        }

        Role defaultRole = roleService.getDefaultUserRole();
        User user = new User(
                dto.email(),
                dto.password(),
                Set.of(defaultRole)
        );

        User savedUser = userRepository.save(user);
        Profile profile = profileService.createProfile(dto.profile() , savedUser);

        return userMapper.toCreateUserResponseDto(savedUser, profile);
    }



    private boolean isUserExist(String email) {
        return userRepository.existsByEmail(email);
    }


}
