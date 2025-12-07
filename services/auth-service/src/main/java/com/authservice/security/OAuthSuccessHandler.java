package com.authservice.security;

import com.authservice.entity.User;
import com.authservice.services.CookieService;
import com.authservice.services.JwtService;
import com.authservice.services.RefreshTokenService;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
@RequiredArgsConstructor
public class OAuthSuccessHandler implements AuthenticationSuccessHandler {

    private final RefreshTokenService refreshTokenService;
    private final JwtService jwtService;
    private final CookieService cookieService;

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException {
        CustomOidcUser oidcUser = (CustomOidcUser) authentication.getPrincipal();
        String newRefreshToken = refreshTokenService.create(oidcUser.getUser());
        User user = oidcUser.getUser();
        String newAccessToken = jwtService.generateToken(
                user.getId().toString(),
                user.getRole()
        );
        Cookie refreshTokenCookie = cookieService.createRefreshTokenCookie(newRefreshToken);

        response.addCookie(refreshTokenCookie);
        response.setStatus(HttpStatus.OK.value());
        response.setContentType("application/json");
        response.getWriter().write(newAccessToken);
    }
}
