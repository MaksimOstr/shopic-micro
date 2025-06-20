package com.authservice.services;

import com.authservice.config.security.model.CustomUserDetails;
import com.authservice.enums.AuthProviderEnum;
import com.authservice.exceptions.NotFoundException;
import com.authservice.services.grpc.UserGrpcService;
import com.shopic.grpc.userservice.UserForAuthGrpcResponse;
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
            UserForAuthGrpcResponse response = userServiceGrpc.getUserForAuth(email);
            AuthProviderEnum provider = AuthProviderEnum.fromString(response.getProvider());

            if(provider != AuthProviderEnum.LOCAL || !response.hasPassword()) {
                throw new UsernameNotFoundException("User was registered with another provider");
            }

            return new CustomUserDetails(
                    response.getEmail(),
                    response.getPassword(),
                    response.getIsAccountNonLocked(),
                    response.getIsVerified(),
                    response.getUserId(),
                    response.getRoleNamesList()
            );
        } catch (NotFoundException e) {
            log.error("User with email {} not found", email);
            throw new UsernameNotFoundException("test");
        }
    }
}

