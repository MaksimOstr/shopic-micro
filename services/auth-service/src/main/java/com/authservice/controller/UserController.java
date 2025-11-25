package com.authservice.controller;

import com.authservice.config.security.model.CustomPrincipal;
import com.authservice.dto.request.ChangePasswordRequest;
import com.authservice.services.user.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/users")
@RequiredArgsConstructor
public class UserController {
    private final UserService userService;

    @PatchMapping("/password")
    public ResponseEntity<Void> changePassword(
            @AuthenticationPrincipal CustomPrincipal principal,
            @RequestBody @Valid ChangePasswordRequest body
    ) {
        userService.changeUserPassword(principal.getId(), body);

        return ResponseEntity.ok().build();
    }
}
