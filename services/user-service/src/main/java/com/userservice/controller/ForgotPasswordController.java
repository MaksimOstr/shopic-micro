package com.userservice.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.userservice.dto.request.ForgotPasswordRequest;
import com.userservice.dto.request.ResetPasswordRequest;
import com.userservice.services.ForgotPasswordService;
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
    ) throws JsonProcessingException {
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
