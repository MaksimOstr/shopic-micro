package com.authservice.services.user;

import com.authservice.dto.request.LocalRegisterRequest;
import com.authservice.dto.response.CreateLocalUserResponse;
import com.authservice.entity.AuthProviderEnum;
import com.authservice.entity.Role;
import com.authservice.entity.User;
import com.authservice.exceptions.AlreadyExistsException;
import com.authservice.mapper.UserMapper;
import com.authservice.repositories.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Set;

@Service
@RequiredArgsConstructor
@Slf4j
public class LocalUserService {
    private final UserRepository userRepository;
    private final RoleService roleService;
    private final UserMapper userMapper;
    private final PasswordService passwordService;
    private final UserQueryService userQueryService;

    private static final String USER_ALREADY_EXISTS = "User with such an email already exists";

    public CreateLocalUserResponse createLocalUser(LocalRegisterRequest dto) {
        if (userQueryService.isUserExist(dto.email())) {
            log.error(USER_ALREADY_EXISTS);
            throw new AlreadyExistsException(USER_ALREADY_EXISTS);
        }

        User user = createLocalUserEntity(dto);
        User savedUser = userRepository.save(user);
        Profile profile = profileService.createProfile(dto.profile(), savedUser);

        return userMapper.toCreateLocalUserResponse(savedUser, profile);
    }

    private User createLocalUserEntity(LocalRegisterRequest dto) {
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
