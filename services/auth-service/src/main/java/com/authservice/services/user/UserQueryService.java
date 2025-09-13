package com.authservice.services.user;

import com.authservice.entity.User;
import com.authservice.exceptions.NotFoundException;
import com.authservice.projection.user.UserForBanProjection;
import com.authservice.repositories.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserQueryService {
    private final UserRepository userRepository;

    private static final String USER_NOT_FOUND = "User was not found";


    public User getUserForAuth(String email) {
        log.info("Auth service received request to get user for auth: {}", email);
        return userRepository.getUserForAuth(email)
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

    public User findUserWithProfileAndRolesById(long id) {
        return userRepository.findWithProfileAndRolesById(id)
                .orElseThrow(() -> new NotFoundException(USER_NOT_FOUND));
    }

    public Optional<User> findOptionalByEmail(String email) {
        return userRepository.findByEmail(email);
    }

    public UserForBanProjection getUserForBan(long id) {
        return userRepository.findUserForBan(id)
                .orElseThrow(() -> new NotFoundException(USER_NOT_FOUND));
    }

    public Page<User> getUserPageBySpec(Pageable pageable, Specification<User> spec) {
        return userRepository.findAll(spec, pageable);
    }
}
