package com.authservice.config.security.handler;

import com.authservice.config.security.model.CustomOidcUser;
import com.authservice.dto.TokenPairDto;
import com.authservice.services.CookieService;
import com.authservice.services.TokenService;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.UUID;

@Component
@RequiredArgsConstructor
public class OAuthSuccessHandler implements AuthenticationSuccessHandler {

    private final TokenService tokenService;
    private final CookieService cookieService;

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException {
        CustomOidcUser oidcUser = (CustomOidcUser) authentication.getPrincipal();
        String deviceId = UUID.randomUUID().toString();

        TokenPairDto tokenResponse = tokenService.getTokenPair(oidcUser.getId(), oidcUser.getRoleNames(), deviceId);
        Cookie refreshTokenCookie = cookieService.createRefreshTokenCookie(tokenResponse.refreshToken());
        Cookie deviceCookie = cookieService.createDeviceCookie(deviceId);

        response.addCookie(refreshTokenCookie);
        response.addCookie(deviceCookie);
        response.setStatus(HttpStatus.OK.value());
        response.setContentType("application/json");
        response.getWriter().write(tokenResponse.accessToken());
    }
}
