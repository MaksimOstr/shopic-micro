package com.authservice.services.user;

import com.authservice.dto.request.CreateOAuthUserRequest;
import com.authservice.dto.response.CreateOAuthUserResponse;
import com.authservice.entity.AuthProviderEnum;
import com.authservice.entity.Role;
import com.authservice.entity.User;
import com.authservice.mapper.UserMapper;
import com.authservice.repositories.UserRepository;
import com.authservice.services.KafkaEventProducer;
import jakarta.validation.Valid;
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
    private final KafkaEventProducer kafkaEventProducer;

    @Transactional
    public CreateOAuthUserResponse createOrGetOAuthUser(@Valid CreateOAuthUserRequest dto) {
        return userQueryService.findOptionalByEmail(dto.email())
                .map(userMapper::toCreateOAuthUserResponse)
                .orElseGet(() -> createOAuthUser(dto));
    }

    private CreateOAuthUserResponse createOAuthUser(CreateOAuthUserRequest dto) {
        User user = createAndSaveUser(dto);

        kafkaEventProducer.sendOAuthUserCreated(dto.firstName(), user.getId(), dto.lastName());

        return userMapper.toCreateOAuthUserResponse(user);
    }

    private User createAndSaveUser(CreateOAuthUserRequest dto) {
        Role defaultRole = roleService.getDefaultUserRole();
        AuthProviderEnum provider = AuthProviderEnum.fromString(dto.provider());
        User user = User.builder()
                .email(dto.email())
                .authProvider(provider)
                .roles(Set.of(defaultRole))
                .build();

        return userRepository.save(user);
    }
}
