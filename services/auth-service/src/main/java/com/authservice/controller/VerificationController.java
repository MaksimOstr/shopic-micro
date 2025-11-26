package com.authservice.controller;

import com.authservice.dto.EmailVerifyRequestDto;
import com.authservice.dto.VerifyUserRequestDto;
import com.authservice.services.user.VerificationService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/verify")
@RequiredArgsConstructor
public class VerificationController {
    private final VerificationService verificationService;

    @PatchMapping
    public ResponseEntity<String> verifyUser(
            @RequestBody @Valid VerifyUserRequestDto body
    ) {
        verificationService.verifyUser(body.code());
        return ResponseEntity.ok("User verified successfully");
    }

    @PostMapping
    public ResponseEntity<String> requestVerificationCode(
            @RequestBody @Valid EmailVerifyRequestDto body
    ) {
        verificationService.requestVerifyEmail(body.email());
        return ResponseEntity.ok("Verification code sent to: " + body.email());
    }
}
