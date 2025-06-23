package com.userservice.controller;

import com.userservice.config.security.model.CustomPrincipal;
import com.userservice.dto.request.ChangeEmailRequest;
import com.userservice.services.EmailChangeRequestService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/email")
@RequiredArgsConstructor
public class EmailController {
    private final EmailChangeRequestService emailChangeRequestService;

    @PostMapping
    public ResponseEntity<Void> requestEmailChange(
            @AuthenticationPrincipal CustomPrincipal principal,
            @RequestBody ChangeEmailRequest body
    ) {

    }

}
