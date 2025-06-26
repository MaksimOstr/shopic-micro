package com.userservice.controller;

import com.userservice.dto.UserDetailsDto;
import com.userservice.dto.UserSummaryDto;
import com.userservice.dto.request.UserParams;
import com.userservice.services.AdminUserService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;


@RestController
@RequestMapping("/admin/users")
@RequiredArgsConstructor
@PreAuthorize("hasRole('ADMIN')")
public class AdminUserController {
    private final AdminUserService adminUserService;

    @GetMapping("/{id}")
    public ResponseEntity<UserDetailsDto> getUser(
            @PathVariable long id
    ) {
        UserDetailsDto details = adminUserService.getUserDetailsById(id);

        return ResponseEntity.ok(details);
    }

    @GetMapping
    public ResponseEntity<Page<UserSummaryDto>> getAllUsers(
            @RequestBody UserParams params
    ) {
        Pageable pageable = PageRequest.of(0, 10);
        Page<UserSummaryDto> userPage = adminUserService.getUserPage(params, pageable);

        return ResponseEntity.ok(userPage);
    }


}
