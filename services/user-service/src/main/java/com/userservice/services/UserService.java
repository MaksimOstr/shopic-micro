package com.userservice.services;

import com.userservice.dto.request.CreateLocalUserRequestDto;
import com.userservice.dto.request.CreateOAuthUserRequestDto;
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

import java.util.List;
import java.util.Set;

import static com.userservice.utils.UserUtils.toUserRolesToRoleNames;


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
        if(isUserExist(dto.getEmail())) {
            log.error(USER_ALREADY_EXISTS);
            throw new EntityAlreadyExistsException(USER_ALREADY_EXISTS);
        }

        Role defaultRole = roleService.getDefaultUserRole();
        User user = new User(
                dto.getEmail(),
                dto.getPassword(),
                Set.of(defaultRole)
        );

        User savedUser = userRepository.save(user);
        Profile profile = profileService.createProfile(dto.getProfile() , savedUser);

        return userMapper.toCreateUserResponseDto(savedUser, profile);
    }

    public CreateOAuthUserResponseDto createOAuthUser(CreateOAuthUserRequestDto dto) {
        return userRepository.findByEmail(dto.getEmail())
                .map(user -> new CreateOAuthUserResponseDto(user.getId(), user.getEmail(), toUserRolesToRoleNames(user.getRoles())))
                .orElseGet(() -> {
                    Role defaultRole = roleService.getDefaultUserRole();
                    User user = new User(
                            dto.getEmail(),
                            AuthProviderEnum.fromString(dto.getProvider()),
                            Set.of(defaultRole)
                    );

                    profileService.createProfile(dto.getProfile() , user);

                    User savedUser = userRepository.save(user);

                    return new CreateOAuthUserResponseDto(savedUser.getId(), user.getEmail(), toUserRolesToRoleNames(user.getRoles()));
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
