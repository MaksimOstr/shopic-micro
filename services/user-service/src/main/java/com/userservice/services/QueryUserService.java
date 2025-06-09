package com.userservice.services;

import com.userservice.entity.User;
import com.userservice.exceptions.EntityDoesNotExistException;
import com.userservice.repositories.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

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
                .orElseThrow(() -> new EntityDoesNotExistException(USER_NOT_FOUND));
    }

    public Set<String> getUserRoleNames(long userId) {
        return userRepository.getUserRoleNames(userId)
                .orElseThrow(() -> new EntityDoesNotExistException(USER_NOT_FOUND));
    }
}
