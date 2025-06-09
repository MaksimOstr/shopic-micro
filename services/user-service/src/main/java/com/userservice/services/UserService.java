package com.userservice.services;

import com.userservice.dto.request.CreateLocalUserRequest;
import com.userservice.dto.request.CreateOAuthUserRequest;
import com.userservice.dto.response.CreateOAuthUserResponseDto;
import com.userservice.dto.response.CreateUserResponseDto;
import com.userservice.entity.AuthProviderEnum;
import com.userservice.entity.Profile;
import com.userservice.entity.Role;
import com.userservice.entity.User;
import com.userservice.exceptions.EntityAlreadyExistsException;
import com.userservice.exceptions.EntityDoesNotExistException;
import com.userservice.mapper.UserMapper;
import com.userservice.repositories.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Set;

import static com.userservice.utils.UserUtils.userRolesToRoleNames;


@Slf4j
@Service
@RequiredArgsConstructor
public class UserService {
    private final UserRepository userRepository;
    private final ProfileService profileService;
    private final RoleService roleService;
    private final UserMapper userMapper;
    private static final String USER_ALREADY_EXISTS = "User with such an email already exists";


    public CreateUserResponseDto createLocalUser(CreateLocalUserRequest dto) {
        if (isUserExist(dto.email())) {
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
        Profile profile = profileService.createProfile(dto.profile(), savedUser);

        return userMapper.toCreateUserResponseDto(savedUser, profile);
    }

    @Transactional
    public CreateOAuthUserResponseDto createOAuthUser(CreateOAuthUserRequest dto) {
        System.out.println(dto.email());
        return userRepository.findByEmail(dto.email())
                .map(user -> new CreateOAuthUserResponseDto(
                        user.getId(),
                        user.getEmail(),
                        user.getAuthProvider(),
                        userRolesToRoleNames(user.getRoles())
                ))
                .orElseGet(() -> {
                    Role defaultRole = roleService.getDefaultUserRole();
                    User user = new User(
                            dto.email(),
                            AuthProviderEnum.fromString(dto.provider()),
                            Set.of(defaultRole)
                    );
                    User savedUser = userRepository.save(user);

                    profileService.createProfile(dto.profile(), savedUser);

                    return new CreateOAuthUserResponseDto(savedUser.getId(), savedUser.getEmail(), savedUser.getAuthProvider(), userRolesToRoleNames(savedUser.getRoles()));
                });
    }

    public User getUserForAuth(String email) {
        log.info("Auth service received request to get user for auth: {}", email);
        return userRepository.getUserForAuth(email)
                .orElseThrow(() -> new EntityDoesNotExistException("User was not found"));
    }

    public Set<String> getUserRoleNames(long userId) {
        return userRepository.getUserRoleNames(userId)
                .orElseThrow(() -> new EntityDoesNotExistException("User was not found"));
    }

    private boolean isUserExist(String email) {
        return userRepository.existsByEmail(email);
    }
}
