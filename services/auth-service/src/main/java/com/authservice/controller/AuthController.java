package com.authservice.controller;

import com.authservice.dto.TokenPairDto;
import com.authservice.dto.request.SignInRequestDto;
import com.authservice.dto.request.RegisterRequestDto;
import com.authservice.dto.response.RegisterResponseDto;
import com.authservice.services.AuthService;
import com.authservice.services.CookieService;
import com.fasterxml.jackson.core.JsonProcessingException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import static com.authservice.services.CookieService.DEVICE_ID_COOKIE_NAME;
import static com.authservice.services.CookieService.REFRESH_TOKEN_COOKIE_NAME;


@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;
    private final CookieService cookieService;


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


    @PostMapping("/logout")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<String> logout(
            @CookieValue(value = REFRESH_TOKEN_COOKIE_NAME) String refreshToken,
            @CookieValue(value = DEVICE_ID_COOKIE_NAME) String deviceId,
            HttpServletResponse response
    ) {
        authService.logout(refreshToken, deviceId);

        Cookie refreshTokenCookie = cookieService.deleteRefreshTokenCookie(refreshToken);
        Cookie deviceIdCookie = cookieService.deleteDeviceCookie(deviceId);

        response.addCookie(refreshTokenCookie);
        response.addCookie(deviceIdCookie);


        String message = "You have been logged out successfully";

        return ResponseEntity.ok(message);
    }
}
