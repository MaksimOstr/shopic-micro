package com.userservice.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.userservice.dto.request.EmailVerifyRequestDto;
import com.userservice.dto.request.VerifyUserRequestDto;
import com.userservice.services.UserVerificationService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;


@RestController
@RequestMapping("/users")
@RequiredArgsConstructor
public class UserController {
    private final UserVerificationService userVerificationService;

    @PatchMapping("/verify")
    public ResponseEntity<String> verifyUser(
            @RequestBody @Valid VerifyUserRequestDto body
    ) {
        userVerificationService.verifyUser(body.code());
        return ResponseEntity.ok("User verified successfully");
    }

    @PostMapping("/request-email-verify")
    public ResponseEntity<String> requestVerificationCode(
            @RequestBody @Valid EmailVerifyRequestDto body
    ) throws JsonProcessingException {
        userVerificationService.requestVerifyEmail(body.email());
        return ResponseEntity.ok("Verification code sent to: " + body.email());
    }
}