package com.banservice.controller;


import com.banservice.config.security.model.CustomPrincipal;
import com.banservice.dto.BanDto;
import com.banservice.dto.request.BanParams;
import com.banservice.dto.request.BanRequest;
import com.banservice.service.BanService;
import jakarta.validation.Valid;
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
@RequestMapping("/bans")
@RequiredArgsConstructor
@PreAuthorize("hasRole('ADMIN')")
public class BanController {
    private final BanService banService;

    @PostMapping
    public ResponseEntity<Void> banUser(
            @RequestBody @Valid BanRequest body,
            @AuthenticationPrincipal CustomPrincipal principal
    ) {
        banService.banUser(body, principal.getId());

        return ResponseEntity.ok().build();
    }

    @PatchMapping("/{id}/unban")
    public ResponseEntity<Void> unbanUser(
            @AuthenticationPrincipal CustomPrincipal principal,
            @PathVariable long id
    ) {
        banService.unbanUser(id, principal.getId());

        return ResponseEntity.ok().build();
    }

    @GetMapping
    public ResponseEntity<Page<BanDto>> getBans(
            @RequestBody BanParams body,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "desc") String sortDirection
    ) {
        Sort.Direction direction = Sort.Direction.fromString(sortDirection);
        Pageable pageable = PageRequest.of(page, size, direction, "createdAt");
        Page<BanDto> bans = banService.getBans(body, pageable);

        return ResponseEntity.ok(bans);
    }

    @GetMapping("/{id}")
    public ResponseEntity<BanDto> getBan(
            @PathVariable long id
    ) {
        BanDto ban = banService.getBan(id);

        return ResponseEntity.ok(ban);
    }
}
