package com.authservice.controller;

import com.authservice.dto.EmailVerifyRequestDto;
import com.authservice.dto.ErrorResponseDto;
import com.authservice.dto.VerifyUserRequestDto;
import com.authservice.services.VerificationService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PatchMapping;
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
@RequestMapping("/api/v1/verify")
@RequiredArgsConstructor
@Tag(name = "Email Verification", description = "Endpoints for issuing and confirming email verification codes")
public class VerificationController {
    private final VerificationService verificationService;

    @Operation(
            summary = "Verify account",
            description = "Validates the submitted verification code and marks the user as verified."
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "User verified.",
                    content = @Content(
                            mediaType = "text/plain",
                            schema = @Schema(type = "string", example = "User verified successfully")
                    )
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Code expired or invalid.",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ErrorResponseDto.class)
                    )
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Verification code or target user not found.",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ErrorResponseDto.class)
                    )
            )
    })
    @PatchMapping
    public ResponseEntity<String> verifyUser(
            @RequestBody @Valid VerifyUserRequestDto body
    ) {
        verificationService.verifyUser(body.code());
        return ResponseEntity.ok("User verified successfully");
    }

    @Operation(
            summary = "Request verification code",
            description = "Creates a verification code for the given email if the user is not verified yet."
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Verification code generated and sent.",
                    content = @Content(
                            mediaType = "text/plain",
                            schema = @Schema(type = "string", example = "Verification code sent to: ada@example.com")
                    )
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Email is already verified.",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ErrorResponseDto.class)
                    )
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "User email not found.",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ErrorResponseDto.class)
                    )
            ),
            @ApiResponse(
                    responseCode = "500",
                    description = "Unable to generate verification code.",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ErrorResponseDto.class)
                    )
            )
    })
    @PostMapping
    public ResponseEntity<String> requestVerificationCode(
            @RequestBody @Valid EmailVerifyRequestDto body
    ) {
        verificationService.requestVerifyEmail(body.email());
        return ResponseEntity.ok("Verification code sent to: " + body.email());
    }
}
