package com.authservice.controller;

import com.authservice.dto.ForgotPasswordRequest;
import com.authservice.dto.ResetPasswordRequest;
import com.authservice.services.user.ForgotPasswordService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/forgot-password")
@RequiredArgsConstructor
public class ForgotPasswordController {
    private final ForgotPasswordService forgotPasswordService;


    @PostMapping
    public ResponseEntity<Void> forgotPasswordRequest(
            @RequestBody @Valid ForgotPasswordRequest body
    ) {
        forgotPasswordService.requestResetPassword(body);

        return ResponseEntity.ok().build();
    }

    @PatchMapping
    public ResponseEntity<Void> resetPassword(
            @RequestBody @Valid ResetPasswordRequest body
    ) {
        forgotPasswordService.resetPassword(body);

        return ResponseEntity.ok().build();
    }
}