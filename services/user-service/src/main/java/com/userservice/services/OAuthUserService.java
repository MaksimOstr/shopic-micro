package com.userservice.services;

import com.userservice.dto.request.CreateOAuthUserRequest;
import com.userservice.dto.response.CreateOAuthUserResponse;
import com.userservice.entity.AuthProviderEnum;
import com.userservice.entity.Role;
import com.userservice.entity.User;
import com.userservice.repositories.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Set;

import static com.userservice.utils.UserUtils.userRolesToRoleNames;


@Service
@RequiredArgsConstructor
public class OAuthUserService {
    private final UserRepository userRepository;
    private final RoleService roleService;
    private final ProfileService profileService;

    @Transactional
    public CreateOAuthUserResponse createOrGetOAuthUser(CreateOAuthUserRequest dto) {
        System.out.println(dto.email());
        return userRepository.findByEmail(dto.email())
                .map(this::mapToOAuthResponse)
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

        return mapToOAuthResponse(savedUser);
    }

    private CreateOAuthUserResponse mapToOAuthResponse(User user) {
        return new CreateOAuthUserResponse(
                user.getId(),
                user.getEmail(),
                user.getAuthProvider(),
                userRolesToRoleNames(user.getRoles())
        );
    }
}
