package com.authservice.services;

import com.authservice.config.security.model.CustomUserDetails;
import com.shopic.grpc.userservice.UserForAuthRequest;
import com.shopic.grpc.userservice.UserForAuthResponse;
import com.shopic.grpc.userservice.UserServiceGrpc;
import io.grpc.StatusRuntimeException;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CustomUserDetailsService implements UserDetailsService {

    private final UserServiceGrpc.UserServiceBlockingStub userServiceGrpc;

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        try {
            UserForAuthRequest request = UserForAuthRequest.newBuilder().setEmail(email).build();
            UserForAuthResponse response = userServiceGrpc.getUserForAuth(request);

            return new CustomUserDetails(
                    response.getEmail(),
                    response.getPassword(),
                    response.getIsBlocked(),
                    response.getIsVerified(),
                    response.getUserId(),
                    response.getRoleNamesList()
            );
        } catch (StatusRuntimeException e) {
            throw new UsernameNotFoundException("User with email " + email + " not found");
        }
    }
}
