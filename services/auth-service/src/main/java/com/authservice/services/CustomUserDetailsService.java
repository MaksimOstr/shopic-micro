package com.authservice.services;

import com.authservice.config.security.model.CustomUserDetails;
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

    private final UserServiceGrpc.UserServiceBlockingStub userServiceGrpc;

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        try {
            log.info("Loading user by email {}", email);
            UserForAuthRequest request = UserForAuthRequest.newBuilder().setEmail(email).build();
            UserForAuthResponse response = userServiceGrpc.getUserForAuth(request);
            System.out.println(response.getPassword());
            System.out.println(response.getEmail());
            System.out.println(response.getIsAccountNonLocked());
            System.out.println(response.getIsVerified());
            return new CustomUserDetails(
                    response.getEmail(),
                    response.getPassword(),
                    response.getIsAccountNonLocked(),
                    response.getIsVerified(),
                    response.getUserId(),
                    response.getRoleNamesList()
            );
        } catch (StatusRuntimeException e) {
            throw new UsernameNotFoundException("User with email " + email + " not found");
        }
    }
}
