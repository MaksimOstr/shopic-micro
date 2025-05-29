package com.authservice.controller;

import com.authservice.dto.TokenPairDto;
import com.authservice.dto.request.SignInRequestDto;
import com.authservice.dto.request.RegisterRequestDto;
import com.authservice.dto.response.RegisterResponseDto;
import com.authservice.services.AuthService;
import com.authservice.services.CookieService;
import com.authservice.services.RotatingJwkManager;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.nimbusds.jose.jwk.JWKSet;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

import static com.authservice.services.CookieService.DEVICE_ID_COOKIE_NAME;
import static com.authservice.services.CookieService.REFRESH_TOKEN_COOKIE_NAME;


@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;
    private final CookieService cookieService;
    private final RotatingJwkManager rotatingJwkManager;

    @PostMapping("/register")

    public ResponseEntity<RegisterResponseDto> register(
            @Valid @RequestBody RegisterRequestDto body
    ) throws JsonProcessingException {
        RegisterResponseDto response = authService.register(body);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/sign-in")
    public ResponseEntity<String> signIn(
            @RequestBody @Valid SignInRequestDto body,
            HttpServletResponse response
    ) {
        System.out.println(body.toString());
        TokenPairDto tokenPair = authService.signIn(body);
        Cookie refreshTokenCookie = cookieService.createRefreshTokenCookie(tokenPair.refreshToken());
        Cookie deviceIdCookie = cookieService.createDeviceCookie(body.deviceId());

        response.addCookie(refreshTokenCookie);
        response.addCookie(deviceIdCookie);

        return ResponseEntity.ok(tokenPair.accessToken());
    }

    @PostMapping("/refresh")
    public ResponseEntity<String> refreshTokens(
            @CookieValue(value = REFRESH_TOKEN_COOKIE_NAME) String refreshToken,
            @CookieValue(value = DEVICE_ID_COOKIE_NAME) String deviceId,
            HttpServletResponse response
    ) {
        TokenPairDto tokenPair = authService.refreshTokens(refreshToken, deviceId);
        Cookie refreshTokenCookie = cookieService.createRefreshTokenCookie(tokenPair.refreshToken());

        response.addCookie(refreshTokenCookie);

        return ResponseEntity.ok(tokenPair.accessToken());
    }

    @GetMapping("/jwk-set")
    public ResponseEntity<Map<String, Object>> getJwk() {
        JWKSet jwkSet = rotatingJwkManager.getPublicJwkSet();

        return ResponseEntity.ok(jwkSet.toJSONObject());
    }
}
