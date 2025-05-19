package com.authservice.services;

import jakarta.servlet.http.Cookie;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class CookieService {

    @Value("${REFRESH_TOKEN_TTL:3600}")
    private int refreshTokenTtl;

    public static final String REFRESH_TOKEN_COOKIE_NAME = "refreshToken";
    public static final String DEVICE_ID_COOKIE_NAME = "deviceId";

    public Cookie createRefreshTokenCookie(String refreshToken) {
        Cookie cookie = new Cookie(REFRESH_TOKEN_COOKIE_NAME, refreshToken);

        cookie.setHttpOnly(true);
        cookie.setMaxAge(refreshTokenTtl);
        cookie.setPath("/");
        cookie.setAttribute("SameSite", "Lax");

        return cookie;
    }

    public Cookie createDeviceCookie(String deviceId) {
        Cookie cookie = new Cookie(DEVICE_ID_COOKIE_NAME, deviceId);

        cookie.setMaxAge(31536000);
        cookie.setPath("/");
        cookie.setHttpOnly(true);
        cookie.setAttribute("SameSite", "Lax");

        return cookie;
    }
}
