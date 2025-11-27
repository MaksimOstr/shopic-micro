package com.authservice.services;

import com.authservice.dto.UserDto;
import com.authservice.dto.ChangePasswordRequest;
import com.authservice.dto.CreateOAuthUserRequest;
import com.authservice.dto.LocalRegisterRequest;
import com.authservice.dto.UpdateUserRequest;
import com.authservice.entity.AuthProviderEnum;
import com.authservice.entity.Role;
import com.authservice.entity.User;
import com.authservice.exception.ApiException;
import com.authservice.exception.NotFoundException;
import com.authservice.mapper.UserMapper;
import com.authservice.repositories.UserRepository;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;
import java.util.Set;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final RoleService roleService;
    private final UserMapper userMapper;

    private static final String USER_NOT_FOUND = "User was not found";

    public User createUser(LocalRegisterRequest dto) {
        if (isUserExist(dto.email())) {
            throw new ApiException("User with such an email already exists", HttpStatus.CONFLICT);
        }

        Role defaultRole = roleService.getRoleByName("ROLE_USER");
        String hashedPassword = passwordEncoder.encode(dto.password());
        User user = User.builder()
                .firstName(dto.firstName())
                .lastName(dto.lastName())
                .email(dto.email())
                .password(hashedPassword)
                .roles(Set.of(defaultRole))
                .authProvider(AuthProviderEnum.LOCAL)
                .build();

        return userRepository.save(user);
    }

    @Transactional
    public User createOrGetOAuthUser(@Valid CreateOAuthUserRequest dto) {
        return findOptionalByEmail(dto.email())
                .orElseGet(() -> {
                    Role defaultRole = roleService.getRoleByName("ROLE_USER");
                    User user = User.builder()
                            .firstName(dto.firstName())
                            .lastName(dto.lastName())
                            .email(dto.email())
                            .roles(Set.of(defaultRole))
                            .authProvider(dto.provider())
                            .build();

                    return userRepository.save(user);
                });
    }

    @Transactional
    public void changeUserPassword(User user, String newPassword) {
        if(passwordEncoder.matches(newPassword, user.getPassword())) {
            throw new ApiException("Password is the same", HttpStatus.BAD_REQUEST);
        }

        String encodedPassword = passwordEncoder.encode(newPassword);
        user.setPassword(encodedPassword);
    }

    @Transactional
    public void changeUserPassword(long id, ChangePasswordRequest dto) {
        User user = findById(id);
        boolean isEqual = passwordEncoder.matches(user.getPassword(), dto.oldPassword());

        if(!isEqual) {
            throw new IllegalArgumentException("Password does not match");
        }

        String encodedNewPassword = passwordEncoder.encode(dto.newPassword());
        user.setPassword(encodedNewPassword);
    }

    @Transactional
    public UserDto updateUser(UpdateUserRequest dto, long userId) {
        User user = findById(userId);

        user.setFirstName(dto.firstName());
        user.setLastName(dto.lastName());

        return userMapper.toDto(user);
    }

    public UserDto getUserDto(long userId) {
        User user = findById(userId);

        return userMapper.toDto(user);
    }

    public User getUserForAuth(String email) {
        log.info("Auth service received request to get user for auth: {}", email);
        return userRepository.findUserWithRolesByEmail(email)
                .orElseThrow(() -> new NotFoundException(USER_NOT_FOUND));
    }

    public boolean isUserExist(String email) {
        return userRepository.existsByEmail(email);
    }

    public User findById(long id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new NotFoundException(USER_NOT_FOUND));
    }

    public User findByEmail(String email) {
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new NotFoundException(USER_NOT_FOUND));
    }

    public void updateVerificationStatus(long userId, boolean verified) {
        int updated = userRepository.markUserVerified(userId, verified);

        if (updated == 0) {
            throw new NotFoundException("User not found");
        }
    }

    public User save(User user) {
        return userRepository.save(user);
    }

    public Optional<User> findOptionalByEmail(String email) {
        return userRepository.findByEmail(email);
    }
}
