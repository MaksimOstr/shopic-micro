package com.authservice.controller;

import com.authservice.dto.LocalRegisterResult;
import com.authservice.dto.TokenPairDto;
import com.authservice.dto.LocalRegisterRequest;
import com.authservice.dto.SignInRequestDto;
import com.authservice.services.AuthService;
import com.authservice.services.CookieService;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;



@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
public class AuthController {
    private final AuthService authService;
    private final CookieService cookieService;


    @PostMapping("/sign-up")
    public ResponseEntity<LocalRegisterResult> registerLocalUser(
            @Valid @RequestBody LocalRegisterRequest body
    ) {
        LocalRegisterResult response = authService.localRegister(body);

        return ResponseEntity.ok(response);
    }


    @PostMapping("/sign-in")
    public ResponseEntity<String> signIn(
            @RequestBody @Valid SignInRequestDto body,
            HttpServletResponse response
    ) {
        TokenPairDto tokenPair = authService.signIn(body);
        Cookie refreshTokenCookie = cookieService.createRefreshTokenCookie(tokenPair.refreshToken());

        response.addCookie(refreshTokenCookie);

        return ResponseEntity.ok(tokenPair.accessToken());
    }


    @PostMapping("/refresh")
    public ResponseEntity<String> refreshTokens(
            @CookieValue("${refresh-token.cookie-name:refresh-token}") String refreshToken,
            HttpServletResponse response
    ) {
        TokenPairDto tokenPair = authService.refreshTokens(refreshToken);
        Cookie refreshTokenCookie = cookieService.createRefreshTokenCookie(tokenPair.refreshToken());

        response.addCookie(refreshTokenCookie);

        return ResponseEntity.ok(tokenPair.accessToken());
    }


    @PostMapping("/logout")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<String> logout(
            @CookieValue("${refresh-token.cookie-name:refresh-token}") String refreshToken,
            HttpServletResponse response
    ) {
        authService.logout(refreshToken);

        Cookie refreshTokenCookie = cookieService.deleteRefreshTokenCookie(refreshToken);

        response.addCookie(refreshTokenCookie);

        String message = "You have been logged out successfully";

        return ResponseEntity.ok(message);
    }
}
