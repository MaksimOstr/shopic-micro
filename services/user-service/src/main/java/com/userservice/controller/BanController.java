package com.userservice.controller;

import com.userservice.config.security.model.CustomPrincipal;
import com.userservice.dto.request.BanRequest;
import com.userservice.services.BanService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/bans")
@PreAuthorize("hasRole('ADMIN')")
public class BanController {
    private final BanService banService;

    @PostMapping()
    public ResponseEntity<Void> banUser(
            @RequestBody @Valid BanRequest body,
            @AuthenticationPrincipal CustomPrincipal principal
            ) {
        banService.banUser(body, principal.getId());

        return ResponseEntity.ok().build();
    }
}
