package com.profileservice.controller;

import com.profileservice.config.security.model.CustomPrincipal;
import com.profileservice.dto.request.UpdateProfileRequest;
import com.profileservice.entity.Profile;
import com.profileservice.service.ProfileService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/profiles")
@PreAuthorize("hasRole('USER')")
public class ProfileController {
    private final ProfileService profileService;

    @PatchMapping
    public ResponseEntity<Void> updateProfile(
            @RequestBody UpdateProfileRequest body,
            @AuthenticationPrincipal CustomPrincipal principal
    ) {
        profileService.editProfile(body, principal.getId());

        return ResponseEntity.ok().build();
    }

}
