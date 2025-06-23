package com.userservice.services;

import com.userservice.entity.User;
import com.userservice.exceptions.NotFoundException;
import com.userservice.projection.EmailVerifyProjection;
import com.userservice.projection.ResetPasswordProjection;
import com.userservice.projection.UserEmailAndPasswordProjection;
import com.userservice.repositories.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.Set;


@Slf4j
@Service
@RequiredArgsConstructor
public class QueryUserService {
    private final UserRepository userRepository;

    private static final String USER_NOT_FOUND = "User was not found";


    public User getUserForAuth(String email) {
        log.info("Auth service received request to get user for auth: {}", email);
        return userRepository.getUserForAuth(email)
                .orElseThrow(() -> new NotFoundException(USER_NOT_FOUND));
    }

    public Set<String> getUserRoleNames(long userId) {
        return userRepository.getUserRoleNames(userId)
                .orElseThrow(() -> new NotFoundException(USER_NOT_FOUND));
    }

    public EmailVerifyProjection getUserForEmailVerify(String email) {
        return userRepository.findUserForEmailVerify(email)
                .orElseThrow(() -> new NotFoundException(USER_NOT_FOUND));
    }

    public boolean isUserExist(String email) {
        return userRepository.existsByEmail(email);
    }

    public ResetPasswordProjection getUserForResetPassword(String email) {
        return userRepository.findUserForResetPassword(email)
                .orElseThrow(() -> new NotFoundException(USER_NOT_FOUND));
    }

    public User findById(long id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new NotFoundException(USER_NOT_FOUND));
    }

    public Optional<User> findOptionalByEmail(String email) {
        return userRepository.findByEmail(email);
    }

    public UserEmailAndPasswordProjection getUserEmailAndPassword(long id) {
        return userRepository.findEmailAndPasswordById(id)
                .orElseThrow(() -> new NotFoundException(USER_NOT_FOUND));
    }
}
