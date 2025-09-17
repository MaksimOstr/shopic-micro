package com.authservice.services.user;

import com.authservice.dto.request.LocalRegisterRequest;
import com.authservice.entity.AuthProviderEnum;
import com.authservice.entity.Role;
import com.authservice.entity.User;
import com.authservice.exceptions.AlreadyExistsException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Set;

@Service
@RequiredArgsConstructor
@Slf4j
public class LocalUserService {
    private final RoleService roleService;
    private final PasswordService passwordService;
    private final UserService userService;

    private static final String USER_ALREADY_EXISTS = "User with such an email already exists";

    public User createLocalUser(LocalRegisterRequest dto) {
        if (userService.isUserExist(dto.email())) {
            log.error(USER_ALREADY_EXISTS);
            throw new AlreadyExistsException(USER_ALREADY_EXISTS);
        }

        return createAndSaveUser(dto);
    }

    private User createAndSaveUser(LocalRegisterRequest dto) {
        Role defaultRole = roleService.getDefaultUserRole();
        String hashedPassword = passwordService.encode(dto.password());

        User user = User.builder()
                .firstName(dto.firstName())
                .lastName(dto.lastName())
                .email(dto.email())
                .password(hashedPassword)
                .roles(Set.of(defaultRole))
                .authProvider(AuthProviderEnum.LOCAL)
                .build();

        return userService.save(user);
    }
}
