package com.authservice.services.user;

import com.authservice.dto.request.CreateOAuthUserRequest;
import com.authservice.dto.response.CreateOAuthUserResponse;
import com.authservice.entity.AuthProviderEnum;
import com.authservice.entity.Role;
import com.authservice.entity.User;
import com.authservice.mapper.UserMapper;
import com.authservice.repositories.UserRepository;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Set;

@Service
@RequiredArgsConstructor
public class OAuthUserService {
    private final UserService userService;
    private final RoleService roleService;
    private final UserMapper userMapper;

    @Transactional
    public CreateOAuthUserResponse createOrGetOAuthUser(@Valid CreateOAuthUserRequest dto) {
        return userService.findOptionalByEmail(dto.email())
                .map(userMapper::toCreateOAuthUserResponse)
                .orElseGet(() -> createOAuthUser(dto));
    }

    private CreateOAuthUserResponse createOAuthUser(CreateOAuthUserRequest dto) {
        User user = createAndSaveUser(dto);

        return userMapper.toCreateOAuthUserResponse(user);
    }

    private User createAndSaveUser(CreateOAuthUserRequest dto) {
        Role defaultRole = roleService.getDefaultUserRole();
        AuthProviderEnum provider = AuthProviderEnum.fromString(dto.provider());
        User user = User.builder()
                .firstName(dto.firstName())
                .lastName(dto.lastName())
                .email(dto.email())
                .authProvider(provider)
                .roles(Set.of(defaultRole))
                .build();

        return userService.save(user);
    }
}
