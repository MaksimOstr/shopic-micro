package com.userservice.services;

import com.userservice.dto.request.CreateOAuthUserRequest;
import com.userservice.dto.response.CreateOAuthUserResponse;
import com.userservice.entity.AuthProviderEnum;
import com.userservice.entity.Role;
import com.userservice.entity.User;
import com.userservice.mapper.UserMapper;
import com.userservice.repositories.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Set;



@Service
@RequiredArgsConstructor
public class OAuthUserService {
    private final UserRepository userRepository;
    private final RoleService roleService;
    private final ProfileService profileService;
    private final QueryUserService queryUserService;
    private final UserMapper userMapper;

    @Transactional
    public CreateOAuthUserResponse createOrGetOAuthUser(CreateOAuthUserRequest dto) {
        return queryUserService.findOptionalByEmail(dto.email())
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

        profileService.createProfile(dto.profile(), savedUser);

        return userMapper.toCreateOAuthUserResponse(savedUser);
    }
}
