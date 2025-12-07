package com.authservice.controller;

import com.authservice.dto.ErrorResponseDto;
import com.authservice.dto.LocalRegisterRequest;
import com.authservice.dto.LocalRegisterResult;
import com.authservice.dto.SignInRequestDto;
import com.authservice.dto.SignInResponse;
import com.authservice.dto.TokenPairDto;
import com.authservice.services.AuthService;
import com.authservice.services.CookieService;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;


@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
@Tag(name = "Auth", description = "Authentication endpoints")
public class AuthController {
    private final AuthService authService;
    private final CookieService cookieService;


    @Operation(
            summary = "Register local user",
            description = "Creates a local account."
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "201",
                    description = "User stored and verification code dispatched.",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = LocalRegisterResult.class)
                    )
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Invalid input data",
                    content = @Content(
                            mediaType = "application/json",
                            examples = {
                                    @ExampleObject(
                                            name = "Invalid email",
                                            value = """
                                                    {
                                                        "email": "must be in email address format"
                                                    }
                                                    """
                                    ),
                                    @ExampleObject(
                                            name = "Passwords do not match",
                                            value = """
                                                    {
                                                        "code": "Bad request",
                                                        "status": 400,
                                                        "message": "Passwords do not match"
                                                    }
                                                    """
                                    )
                            }
                    )
            ),
            @ApiResponse(
                    responseCode = "409",
                    description = "User with the same email already exists.",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(
                                    name = "User with email already exists",
                                    implementation = ErrorResponseDto.class
                            )
                    )
            ),
            @ApiResponse(
                    responseCode = "500",
                    description = "Code generation failed",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ErrorResponseDto.class)
                    )
            )
    })
    @PostMapping("/sign-up")
    public ResponseEntity<LocalRegisterResult> registerLocalUser(
            @Valid @RequestBody LocalRegisterRequest body
    ) {
        LocalRegisterResult response = authService.localRegister(body);

        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }


    @Operation(
            summary = "Sign in",
            description = "Authenticates the user, issues a new access token in the response body, and refresh token in cookie."
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Authentication successful.",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = SignInResponse.class)
                    )
            ),
            @ApiResponse(responseCode = "400", description = "Invalid input data",
                    content = @Content(
                            examples = @ExampleObject(
                                    value = """
                                                {
                                                 "email": "must be in email address format",
                                                }
                                            """)
                    )),
            @ApiResponse(
                    responseCode = "401",
                    description = "Authentication failed (bad credentials, disabled account, etc.).",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ErrorResponseDto.class)
                    )
            )
    })
    @PostMapping("/sign-in")
    public ResponseEntity<SignInResponse> signIn(
            @RequestBody @Valid SignInRequestDto body,
            HttpServletResponse response
    ) {
        TokenPairDto tokenPair = authService.signIn(body);
        Cookie refreshTokenCookie = cookieService.createRefreshTokenCookie(tokenPair.refreshToken());

        response.addCookie(refreshTokenCookie);

        return ResponseEntity.ok(new SignInResponse(tokenPair.accessToken()));
    }


    @Operation(
            summary = "Refresh access token",
            description = "Validates the refresh token cookie, rotates it, and issues a new access token."
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Refresh succeeded.",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = SignInResponse.class)
                    )
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Refresh token cookie missing.",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ErrorResponseDto.class)
                    )
            ),
            @ApiResponse(
                    responseCode = "401",
                    description = "Refresh token invalid or expired.",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ErrorResponseDto.class)
                    )
            )
    })
    @PostMapping("/refresh")
    public ResponseEntity<SignInResponse> refreshTokens(
            @CookieValue("${refresh-token.cookie-name:refresh-token}") String refreshToken,
            HttpServletResponse response
    ) {
        TokenPairDto tokenPair = authService.refreshTokens(refreshToken);
        Cookie refreshTokenCookie = cookieService.createRefreshTokenCookie(tokenPair.refreshToken());

        response.addCookie(refreshTokenCookie);

        return ResponseEntity.ok(new SignInResponse(tokenPair.accessToken()));
    }


    @Operation(
            summary = "Logout",
            description = "Invalidates the refresh token and clears the cookie."
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Logout completed, cookie cleared."
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Refresh token cookie missing.",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ErrorResponseDto.class)
                    )
            ),
            @ApiResponse(
                    responseCode = "401",
                    description = "User not authenticated.",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ErrorResponseDto.class)
                    )
            )
    })
    @PostMapping("/logout")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<Void> logout(
            @CookieValue("${refresh-token.cookie-name:refresh-token}") String refreshToken,
            HttpServletResponse response
    ) {
        authService.logout(refreshToken);

        Cookie refreshTokenCookie = cookieService.deleteRefreshTokenCookie(refreshToken);

        response.addCookie(refreshTokenCookie);

        return ResponseEntity.ok().build();
    }
}
