package com.userservice.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.userservice.dto.request.EmailVerifyRequestDto;
import com.userservice.dto.request.VerifyUserRequestDto;
import com.userservice.services.VerificationService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/verify")
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

    @PostMapping("/request")
    public ResponseEntity<String> requestVerificationCode(
            @RequestBody @Valid EmailVerifyRequestDto body
    ) {
        verificationService.requestVerifyEmail(body.email());
        return ResponseEntity.ok("Verification code sent to: " + body.email());
    }
}
