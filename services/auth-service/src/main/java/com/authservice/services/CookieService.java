package com.authservice.services;

import jakarta.servlet.http.Cookie;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class CookieService {

    @Value("${REFRESH_TOKEN_TTL:3600}")
    private int refreshTokenTtl;

    public static final String REFRESH_TOKEN_COOKIE_NAME = "refreshToken";


    public Cookie createRefreshTokenCookie(String refreshToken) {
        Cookie cookie = new Cookie(REFRESH_TOKEN_COOKIE_NAME, refreshToken);

        cookie.setHttpOnly(true);
        cookie.setMaxAge(refreshTokenTtl);
        cookie.setPath("/");
        cookie.setAttribute("SameSite", "Lax");

        return cookie;
    }

    public Cookie deleteRefreshTokenCookie(String refreshToken) {
        Cookie cookie = new Cookie(REFRESH_TOKEN_COOKIE_NAME, refreshToken);

        cookie.setHttpOnly(true);
        cookie.setMaxAge(0);
        cookie.setPath("/");
        cookie.setAttribute("SameSite", "Lax");

        return cookie;
    }
}
