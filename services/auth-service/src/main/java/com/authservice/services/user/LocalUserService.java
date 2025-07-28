package com.authservice.services.user;

import com.authservice.dto.request.LocalRegisterRequest;
import com.authservice.dto.response.CreateLocalUserResponse;
import com.authservice.entity.AuthProviderEnum;
import com.authservice.entity.Role;
import com.authservice.entity.User;
import com.authservice.exceptions.AlreadyExistsException;
import com.authservice.repositories.UserRepository;
import com.authservice.services.KafkaEventProducer;
import com.authservice.services.grpc.GrpcCodeService;
import com.shopic.grpc.codeservice.CreateCodeResponse;
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
    private final PasswordService passwordService;
    private final UserQueryService userQueryService;
    private final KafkaEventProducer kafkaEventProducer;
    private final GrpcCodeService grpcCodeService;

    private static final String USER_ALREADY_EXISTS = "User with such an email already exists";

    public CreateLocalUserResponse createLocalUser(LocalRegisterRequest dto) {
        if (userQueryService.isUserExist(dto.email())) {
            log.error(USER_ALREADY_EXISTS);
            throw new AlreadyExistsException(USER_ALREADY_EXISTS);
        }

        User user = createAndSaveUser(dto);

        getCodeAndSendEvent(user, dto);

        return new CreateLocalUserResponse(
                user.getId(),
                user.getEmail(),
                user.getCreatedAt()
        );
    }

    private User createAndSaveUser(LocalRegisterRequest dto) {
        Role defaultRole = roleService.getDefaultUserRole();
        String hashedPassword = passwordService.encode(dto.password());

        User user = User.builder()
                .email(dto.email())
                .password(hashedPassword)
                .roles(Set.of(defaultRole))
                .authProvider(AuthProviderEnum.LOCAL)
                .build();

        return userRepository.save(user);
    }

    private void getCodeAndSendEvent(User user, LocalRegisterRequest dto) {
        CreateCodeResponse response = grpcCodeService.getEmailVerificationCode(user.getId());

        kafkaEventProducer.sendLocalUserCreated(
                user.getEmail(),
                response.getCode(),
                dto.firstName(),
                user.getId(),
                dto.lastName()
        );
    }
}
