package com.profileservice.controller;

import com.profileservice.config.security.model.CustomPrincipal;
import com.profileservice.dto.request.UpdateProfileRequest;
import com.profileservice.entity.Profile;
import com.profileservice.service.ProfileService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/profiles")
@PreAuthorize("hasRole('USER')")
public class ProfileController {
    private final ProfileService profileService;

    @GetMapping("/me")
    @PreAuthorize("hasAnyRole('USER')")
    public ResponseEntity<Profile> getProfile(
            @AuthenticationPrincipal CustomPrincipal principal
    ) {
        Profile profile = profileService.getProfileByUserId(principal.getId());

        return ResponseEntity.ok(profile);
    }

    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Profile> getProfileById(
            @RequestParam Long userId
    ) {
        Profile profile = profileService.getProfileByUserId(userId);

        return ResponseEntity.ok(profile);
    }

    @PatchMapping
    public ResponseEntity<Void> updateProfile(
            @RequestBody UpdateProfileRequest body,
            @AuthenticationPrincipal CustomPrincipal principal
    ) {
        profileService.editProfile(body, principal.getId());

        return ResponseEntity.ok().build();
    }

}
