package com.authservice.services.user;

import com.authservice.dto.request.CreateOAuthUserRequest;
import com.authservice.dto.response.CreateOAuthUserResponse;
import com.authservice.entity.AuthProviderEnum;
import com.authservice.entity.Role;
import com.authservice.entity.User;
import com.authservice.mapper.UserMapper;
import com.authservice.repositories.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Set;

@Service
@RequiredArgsConstructor
public class OAuthUserService {
    private final UserRepository userRepository;
    private final RoleService roleService;
    private final UserQueryService userQueryService;
    private final UserMapper userMapper;

    @Transactional
    public CreateOAuthUserResponse createOrGetOAuthUser(CreateOAuthUserRequest dto) {
        return userQueryService.findOptionalByEmail(dto.email())
                .map(userMapper::toCreateOAuthUserResponse)
                .orElseGet(() -> createOAuthUser(dto));
    }

    private CreateOAuthUserResponse createOAuthUser(CreateOAuthUserRequest dto) {
        Role defaultRole = roleService.getDefaultUserRole();
        AuthProviderEnum provider = AuthProviderEnum.fromString(dto.provider());
        User user = User.builder()
                .email(dto.email())
                .authProvider(provider)
                .roles(Set.of(defaultRole))
                .build();
        User savedUser = userRepository.save(user);



        return userMapper.toCreateOAuthUserResponse(savedUser);
    }
}
