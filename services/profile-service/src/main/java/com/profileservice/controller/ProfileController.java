package com.profileservice.controller;

import com.profileservice.config.security.model.CustomPrincipal;
import com.profileservice.dto.ProfileDto;
import com.profileservice.dto.request.ProfileParams;
import com.profileservice.dto.request.UpdateProfileRequest;
import com.profileservice.entity.Profile;
import com.profileservice.service.ProfileService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
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
    public ResponseEntity<Profile> getProfile(
            @AuthenticationPrincipal CustomPrincipal principal
    ) {
        Profile profile = profileService.getProfileByUserId(principal.getId());

        return ResponseEntity.ok(profile);
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ProfileDto> getProfileByUserId(
            @PathVariable Long id
    ) {
        ProfileDto profile = profileService.getProfileDtoById(id);

        return ResponseEntity.ok(profile);
    }

    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Page<ProfileDto>> getProfileDtoPage(
            @RequestBody ProfileParams params,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "desc") String sortDirection

    ) {
        Sort.Direction direction = Sort.Direction.fromString(sortDirection);
        Pageable pageable = PageRequest.of(page, size, direction, "createdAt");
        Page<ProfileDto> profilePage = profileService.getProfileDtoPage(params, pageable);

        return ResponseEntity.ok(profilePage);
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
