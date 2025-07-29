package com.authservice.controller;

import com.authservice.config.security.model.CustomPrincipal;
import com.authservice.dto.request.ChangePasswordRequest;
import com.authservice.services.user.PasswordService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/password")
@RequiredArgsConstructor
public class PasswordController {
    private final PasswordService passwordService;

    @PatchMapping("/change")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<Void> changePassword(
            @AuthenticationPrincipal CustomPrincipal principal,
            @RequestBody @Valid ChangePasswordRequest body
    ) {
        passwordService.changePassword(body, principal.getId());

        return ResponseEntity.ok().build();
    }
}
