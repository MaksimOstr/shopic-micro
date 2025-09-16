package com.authservice.controller;

import com.authservice.config.security.model.CustomPrincipal;
import com.authservice.dto.UserProfileResponse;
import com.authservice.dto.request.UpdateProfileRequest;
import com.authservice.services.user.ProfileService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/users")
@PreAuthorize("hasRole('USER')")
public class ProfileController {

    private final ProfileService profileService;

    @GetMapping("/me")
    public ResponseEntity<UserProfileResponse> getSelfUser(
            @AuthenticationPrincipal CustomPrincipal principal
    ) {
        UserProfileResponse profile = profileService.getProfile(principal.getId());

        return ResponseEntity.ok(profile);
    }


    @PatchMapping
    public ResponseEntity<UserProfileResponse> updateProfile(
            @RequestBody UpdateProfileRequest body,
            @AuthenticationPrincipal CustomPrincipal principal
    ) {
        UserProfileResponse profile = profileService.updateProfile(body, principal.getId());

        return ResponseEntity.ok(profile);
    }
}
