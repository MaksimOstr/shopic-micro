package com.userservice.controller;

import com.userservice.dto.request.VerifyUserRequestDto;
import com.userservice.services.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;


@RestController
@RequestMapping("/users")
@RequiredArgsConstructor
public class UserController {
    private final UserService userService;

    @PatchMapping("/verify")
    public ResponseEntity<String> verifyUser(
            @RequestBody @Valid VerifyUserRequestDto body
    ) {
        userService.verifyUser(body.code());
        return ResponseEntity.ok("User verified successfully");
    }

    @PostMapping("/request-email-verify")
    public ResponseEntity<String> requestVerificationCode() {
        return ResponseEntity.ok("Verification code sent");
    }
}