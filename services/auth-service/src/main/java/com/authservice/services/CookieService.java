package com.authservice.services;

import com.authservice.config.properties.RefreshTokenProperties;
import jakarta.servlet.http.Cookie;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CookieService {
    private final RefreshTokenProperties refreshTokenProperties;


    public Cookie createRefreshTokenCookie(String refreshToken) {
        Cookie cookie = new Cookie(refreshTokenProperties.getCookieName(), refreshToken);

        cookie.setHttpOnly(true);
        cookie.setMaxAge(refreshTokenProperties.getExpiresAt());
        cookie.setPath("/");
        cookie.setAttribute("SameSite", "Lax");

        return cookie;
    }

    public Cookie deleteRefreshTokenCookie(String refreshToken) {
        Cookie cookie = new Cookie(refreshTokenProperties.getCookieName(), refreshToken);

        cookie.setHttpOnly(true);
        cookie.setMaxAge(0);
        cookie.setPath("/");
        cookie.setAttribute("SameSite", "Lax");

        return cookie;
    }
}
