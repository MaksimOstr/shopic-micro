package com.userservice.controller;

import com.userservice.config.security.model.CustomPrincipal;
import com.userservice.dto.request.ProfileParams;
import com.userservice.entity.Profile;
import com.userservice.services.ProfileService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/profiles")
@RequiredArgsConstructor
public class ProfileController {
    private final ProfileService profileService;

    @GetMapping
    @PreAuthorize("hasAnyRole('USER')")
    public ResponseEntity<Profile> getProfile(
            @AuthenticationPrincipal CustomPrincipal principal
    ) {
        Profile profile = profileService.getProfileByUserId(principal.getId());

        return ResponseEntity.ok(profile);
    }

    @PatchMapping
    @PreAuthorize("hasAnyRole('USER')")
    public ResponseEntity<Void> editProfile(
            @RequestBody ProfileParams params,
            @AuthenticationPrincipal CustomPrincipal principal
    ) {
        profileService.editProfile(params, principal.getId());

        return ResponseEntity.ok().build();
    }
}
