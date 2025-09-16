package com.authservice.controller;

import com.authservice.config.security.model.CustomPrincipal;
import com.authservice.dto.request.ChangeEmailRequest;
import com.authservice.services.user.EmailChangeService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/email")
@RequiredArgsConstructor
public class EmailController {
    private final EmailChangeService emailChangeRequestService;

    @PostMapping("/change-request")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<Void> requestEmailChange(
            @AuthenticationPrincipal CustomPrincipal principal,
            @RequestBody @Valid ChangeEmailRequest body
    ) {
        emailChangeRequestService.createRequest(body, principal.getId());

        return ResponseEntity.ok().build();
    }

    @PatchMapping("/change")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<Void> changeEmail(
            @RequestParam String code
    ) {

        emailChangeRequestService.changeEmail(code);

        return ResponseEntity.ok().build();
    }

}
