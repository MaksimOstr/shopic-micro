package com.userservice.services;

import com.userservice.dto.request.CreateLocalUserRequest;
import com.userservice.dto.response.CreateLocalUserResponse;
import com.userservice.entity.AuthProviderEnum;
import com.userservice.entity.Profile;
import com.userservice.entity.Role;
import com.userservice.entity.User;
import com.userservice.exceptions.EntityAlreadyExistsException;
import com.userservice.mapper.UserMapper;
import com.userservice.repositories.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Set;

@Service
@RequiredArgsConstructor
@Slf4j
public class LocalUserService {
    private final UserRepository userRepository;
    private final ProfileService profileService;
    private final RoleService roleService;
    private final UserMapper userMapper;
    private final PasswordService passwordService;
    private final QueryUserService queryUserService;

    private static final String USER_ALREADY_EXISTS = "User with such an email already exists";

    public CreateLocalUserResponse createLocalUser(CreateLocalUserRequest dto) {
        if (queryUserService.isUserExist(dto.email())) {
            log.error(USER_ALREADY_EXISTS);
            throw new EntityAlreadyExistsException(USER_ALREADY_EXISTS);
        }

        User user = createLocalUserEntity(dto);
        User savedUser = userRepository.save(user);
        Profile profile = profileService.createProfile(dto.profile(), savedUser);

        return userMapper.toCreateUserResponseDto(savedUser, profile);
    }

    private User createLocalUserEntity(CreateLocalUserRequest dto) {
        Role defaultRole = roleService.getDefaultUserRole();
        String hashedPassword = passwordService.encode(dto.password());

        return User.builder()
                .email(dto.email())
                .password(hashedPassword)
                .roles(Set.of(defaultRole))
                .authProvider(AuthProviderEnum.LOCAL)
                .build();
    }
}
