package com.userservice.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.userservice.dto.request.ChangePasswordRequest;
import com.userservice.dto.request.ResetPasswordRequest;
import com.userservice.services.UserPasswordService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/password")
@RequiredArgsConstructor
public class PasswordController {
    private final UserPasswordService userPasswordService;

    @PostMapping("/request")
    public ResponseEntity<Void> requestPassword(
            @RequestBody @Valid ResetPasswordRequest body
    ) throws JsonProcessingException {
        userPasswordService.requestResetPassword(body);

        return ResponseEntity.ok().build();
    }

    @PatchMapping("/change")
    public ResponseEntity<Void> changePassword(
            @RequestParam("code") String code,
            @RequestBody @Valid ChangePasswordRequest body
    ) {
        userPasswordService.resetPassword(body.newPassword(), code);

        return ResponseEntity.ok().build();
    }

}
