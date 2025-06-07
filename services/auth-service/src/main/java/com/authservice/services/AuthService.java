package com.authservice.services;

import com.authservice.config.security.model.CustomUserDetails;
import com.authservice.dto.TokenPairDto;
import com.authservice.dto.request.LocalRegisterRequest;
import com.authservice.dto.request.OAuthRegisterRequest;
import com.authservice.dto.request.SignInRequestDto;
import com.authservice.dto.response.LocalRegisterResponse;

import com.authservice.dto.response.OAuthRegisterResponse;
import com.fasterxml.jackson.core.JsonProcessingException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

import java.util.Set;

import static com.authservice.utils.AuthUtils.mapUserRoles;


@Slf4j
@Service
@RequiredArgsConstructor
public class AuthService {
    private final AuthenticationManager authenticationManager;
    private final TokenService tokenService;
    private final LocalAuthProvider localAuthProvider;
    private final OAuthProvider oAuthProvider;


    public LocalRegisterResponse localRegister(LocalRegisterRequest dto) throws JsonProcessingException {
        return localAuthProvider.register(dto);
    }

    public OAuthRegisterResponse oAuthRegister(OAuthRegisterRequest dto) throws JsonProcessingException {
        return oAuthProvider.register(dto);
    }

    public TokenPairDto signIn(SignInRequestDto dto) {
        Authentication authReq = new UsernamePasswordAuthenticationToken(dto.email(), dto.password());
        Authentication authenticatedUser = authenticationManager.authenticate(authReq);
        CustomUserDetails customUserDetails = (CustomUserDetails) authenticatedUser.getPrincipal();

        long userId = customUserDetails.getUserId();
        Set<String> roles = mapUserRoles(customUserDetails.getAuthorities());

        return tokenService.getTokenPair(userId, roles, dto.deviceId());
    }


    public TokenPairDto refreshTokens(String refreshToken, String deviceId) {
        return tokenService.refreshTokens(refreshToken, deviceId);
    }

    public void logout(String refreshToken, String deviceId) {
        tokenService.logout(refreshToken, deviceId);
    }

}
