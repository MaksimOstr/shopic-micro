package com.userservice.controller;

import com.userservice.config.security.model.CustomPrincipal;
import com.userservice.dto.request.ChangePasswordRequest;
import com.userservice.services.PasswordService ;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

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
