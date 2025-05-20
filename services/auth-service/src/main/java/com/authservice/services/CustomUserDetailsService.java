package com.authservice.services;

import com.authservice.config.security.model.CustomUserDetails;
import com.authservice.exceptions.EntityDoesNotExistException;
import com.shopic.grpc.userservice.UserForAuthRequest;
import com.shopic.grpc.userservice.UserForAuthResponse;
import com.shopic.grpc.userservice.UserServiceGrpc;
import io.grpc.StatusRuntimeException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class CustomUserDetailsService implements UserDetailsService {

    private final UserGrpcService userServiceGrpc;

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        try {
            log.info("Loading user by email {}", email);
            UserForAuthResponse response = userServiceGrpc.getUserForAuth(email);

            return new CustomUserDetails(
                    response.getEmail(),
                    response.getPassword(),
                    response.getIsAccountNonLocked(),
                    response.getIsVerified(),
                    response.getUserId(),
                    response.getRoleNamesList()
            );
        } catch (EntityDoesNotExistException e) {
            throw new UsernameNotFoundException(e.getMessage());
        }
    }
}
