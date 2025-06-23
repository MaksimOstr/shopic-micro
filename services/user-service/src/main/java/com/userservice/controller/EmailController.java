package com.userservice.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.userservice.config.security.model.CustomPrincipal;
import com.userservice.dto.request.ChangeEmailRequest;
import com.userservice.services.EmailChangeRequestService;
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
    private final EmailChangeRequestService emailChangeRequestService;

    @PostMapping("/change-request")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<Void> requestEmailChange(
            @AuthenticationPrincipal CustomPrincipal principal,
            @RequestBody @Valid ChangeEmailRequest body
    ) throws JsonProcessingException {
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
