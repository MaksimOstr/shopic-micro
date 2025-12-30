package com.authservice.controller;

import com.authservice.dto.ErrorResponseDto;
import com.authservice.dto.ForgotPasswordRequest;
import com.authservice.dto.MessageResponseDto;
import com.authservice.dto.ResetPasswordRequest;
import com.authservice.services.ForgotPasswordService;
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
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;

@RestController
@RequestMapping("/api/v1/forgot-password")
@RequiredArgsConstructor
@Tag(name = "Password Recovery", description = "Endpoints to request and complete password reset")
public class ForgotPasswordController {
    private final ForgotPasswordService forgotPasswordService;


    @Operation(
            summary = "Request reset code",
            description = "Generates a reset-password code"
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Reset code generated and sent."
            ),
            @ApiResponse(
                    responseCode = "500",
                    description = "Unable to generate reset code.",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ErrorResponseDto.class)
                    )
            )
    })
    @PostMapping
    public ResponseEntity<MessageResponseDto> forgotPasswordRequest(
            @RequestBody @Valid ForgotPasswordRequest body
    ) {
        forgotPasswordService.requestResetPassword(body);
        String message = "Reset password instructions have been sent if applicable.";

        return ResponseEntity.ok(new MessageResponseDto(message));
    }

    @Operation(
            summary = "Reset password",
            description = "Validates the reset code and assigns the new password."
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Password reset completed."
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Validation failed, code expired, or new password same as old.",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ErrorResponseDto.class)
                    )
            )
    })
    @PatchMapping
    public ResponseEntity<Void> resetPassword(
            @RequestBody @Valid ResetPasswordRequest body
    ) {
        forgotPasswordService.resetPassword(body);

        return ResponseEntity.ok().build();
    }
}
