package com.authservice.services;

import com.authservice.config.security.model.CustomUserDetails;
import com.authservice.entity.AuthProviderEnum;
import com.authservice.exceptions.NotFoundException;
import com.authservice.entity.User;
import com.authservice.services.grpc.GrpcBanService;
import com.authservice.services.user.UserQueryService;
import com.shopic.grpc.banservice.CheckUserBanResponse;
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

    private final UserQueryService userQueryService;
    private final GrpcBanService grpcBanService;

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        try {
            log.info("Loading user by email {}", email);
            User user = userQueryService.getUserForAuth(email);

            if(user.getAuthProvider() != AuthProviderEnum.LOCAL) {
                throw new UsernameNotFoundException("User was registered with another provider");
            }

            CheckUserBanResponse response = grpcBanService.checkUserBan(user.getId());
            System.out.println(response.getIsBanned());
            return new CustomUserDetails(user, !response.getIsBanned());
        } catch (NotFoundException e) {
            log.error("User with email {} not found", email);
            throw new UsernameNotFoundException("test");
        }
    }
}

