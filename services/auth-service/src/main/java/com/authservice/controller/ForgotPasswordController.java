package com.authservice.controller;

import com.authservice.dto.ErrorResponseDto;
import com.authservice.dto.ForgotPasswordRequest;
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
import io.swagger.v3.oas.annotations.media.ExampleObject;
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
                    responseCode = "400",
                    description = "Validation failed or account is not a local account.",
                    content = @Content(
                            mediaType = "application/json",
                            examples = {
                                    @ExampleObject(
                                            name = "ValidationError",
                                            value = "{\"email\":\"must be a well-formed email address\"}"
                                    ),
                                    @ExampleObject(
                                            name = "NonLocalAccount",
                                            value = """
                                                    {
                                                      "code": "Bad Request",
                                                      "status": 400,
                                                      "message": "User is not a local user"
                                                    }
                                                    """
                                    )
                            }
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
                    description = "Unable to generate reset code.",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ErrorResponseDto.class),
                            examples = @ExampleObject(
                                    name = "CodeGenerationFailed",
                                    value = """
                                            {
                                              "code": "Internal Server Error",
                                              "status": 500,
                                              "message": "Code generation failed."
                                            }
                                            """
                            )
                    )
            )
    })
    @PostMapping
    public ResponseEntity<Void> forgotPasswordRequest(
            @RequestBody @Valid ForgotPasswordRequest body
    ) {
        forgotPasswordService.requestResetPassword(body);

        return ResponseEntity.ok().build();
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
