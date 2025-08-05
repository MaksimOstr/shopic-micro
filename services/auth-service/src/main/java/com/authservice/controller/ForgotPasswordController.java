package com.authservice.controller;

import com.authservice.dto.request.ForgotPasswordRequest;
import com.authservice.dto.request.ResetPasswordRequest;
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

    @PostMapping("/request")
    public ResponseEntity<Void> forgotPasswordRequest(
            @RequestBody @Valid ForgotPasswordRequest body
    ) {
        forgotPasswordService.requestResetPassword(body);

        return ResponseEntity.ok().build();
    }

    @PatchMapping("/reset")
    public ResponseEntity<Void> resetPassword(
            @RequestParam("code") String code,
            @RequestBody @Valid ResetPasswordRequest body
    ) {
        forgotPasswordService.resetPassword(body.newPassword(), code);

        return ResponseEntity.ok().build();
    }
}