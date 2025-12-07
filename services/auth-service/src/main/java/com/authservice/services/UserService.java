package com.authservice.services;

import com.authservice.dto.UserDto;
import com.authservice.dto.ChangePasswordRequest;
import com.authservice.dto.LocalRegisterRequest;
import com.authservice.entity.AuthProviderEnum;
import com.authservice.entity.UserRolesEnum;
import com.authservice.entity.User;
import com.authservice.exception.ApiException;
import com.authservice.exception.NotFoundException;
import com.authservice.mapper.UserMapper;
import com.authservice.repositories.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;
import java.util.UUID;


@Slf4j
@Service
@RequiredArgsConstructor
public class UserService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final UserMapper userMapper;

    private static final String USER_NOT_FOUND = "User was not found";

    public User createUser(LocalRegisterRequest dto) {
        if(!dto.password().equals(dto.confirmPassword())){
            throw new ApiException("Passwords do not match", HttpStatus.BAD_REQUEST);
        }

        if (isUserExist(dto.email())) {
            throw new ApiException("User with such an email already exists", HttpStatus.CONFLICT);
        }

        String hashedPassword = passwordEncoder.encode(dto.password());
        User user = User.builder()
                .email(dto.email())
                .password(hashedPassword)
                .role(UserRolesEnum.ROLE_USER)
                .authProvider(AuthProviderEnum.LOCAL)
                .build();

        return userRepository.save(user);
    }

    @Transactional
    public User createOrGetOAuthUser(AuthProviderEnum provider,  String email) {
        return findOptionalByEmail(email)
                .orElseGet(() -> {
                    User user = User.builder()
                            .email(email)
                            .role(UserRolesEnum.ROLE_USER)
                            .authProvider(provider)
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
    public void changeUserPassword(UUID id, ChangePasswordRequest dto) {
        User user = findById(id);
        boolean isEqual = passwordEncoder.matches(dto.oldPassword(), user.getPassword());

        if(!isEqual) {
            throw new ApiException("Password does not match", HttpStatus.BAD_REQUEST);
        }

        String encodedNewPassword = passwordEncoder.encode(dto.newPassword());
        user.setPassword(encodedNewPassword);
    }

    public UserDto getUserDto(UUID userId) {
        User user = findById(userId);

        return userMapper.toDto(user);
    }

    public boolean isUserExist(String email) {
        return userRepository.existsByEmail(email);
    }

    public User findById(UUID id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new NotFoundException(USER_NOT_FOUND));
    }

    public User findByEmail(String email) {
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new NotFoundException(USER_NOT_FOUND));
    }

    public void updateVerificationStatus(UUID userId, boolean verified) {
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
