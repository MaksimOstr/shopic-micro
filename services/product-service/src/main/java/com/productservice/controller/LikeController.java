package com.productservice.controller;

import com.productservice.config.security.model.CustomPrincipal;
import com.productservice.services.LikeService;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/likes")
public class LikeController {
    private final LikeService likeService;

    @PostMapping
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<Object> toggleLike(
        @RequestParam @NotNull long productId,
        @AuthenticationPrincipal CustomPrincipal principal
    ) {
        likeService.toggleLike(productId, principal.getId());

        return ResponseEntity.ok().build();
    }

    @GetMapping("/count")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<Integer> getLikeCount(
            @RequestParam @NotNull long productId
    ) {
        int likeCount = likeService.getLikeCount(productId);

        return ResponseEntity.ok(likeCount);
    }
}
