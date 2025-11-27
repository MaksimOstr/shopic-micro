package com.authservice.security;

import com.authservice.entity.AuthProviderEnum;
import com.authservice.exceptions.NotFoundException;
import com.authservice.entity.User;
import com.authservice.services.UserService;
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

    private final UserService userQueryService;

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        try {
            log.info("Loading user by email {}", email);
            User user = userQueryService.getUserForAuth(email);

            if(user.getAuthProvider() != AuthProviderEnum.LOCAL) {
                throw new UsernameNotFoundException("User was registered with another provider");
            }

            return new CustomUserDetails(user);
        } catch (NotFoundException e) {
            log.error("User with email {} not found", email);
            throw new UsernameNotFoundException("test");
        }
    }
}

