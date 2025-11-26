package com.authservice.controller;

import com.authservice.security.CustomPrincipal;
import com.authservice.dto.UserDto;
import com.authservice.dto.ChangePasswordRequest;
import com.authservice.dto.UpdateUserRequest;
import com.authservice.services.user.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/users")
@RequiredArgsConstructor
public class UserController {
    private final UserService userService;

    @PatchMapping("/password")
    public ResponseEntity<Void> changePassword(
            @AuthenticationPrincipal CustomPrincipal principal,
            @RequestBody @Valid ChangePasswordRequest body
    ) {
        userService.changeUserPassword(principal.getId(), body);

        return ResponseEntity.ok().build();
    }

    @GetMapping("/me")
    public ResponseEntity<UserDto> getUserInfo(
            @AuthenticationPrincipal CustomPrincipal principal
    ) {
        UserDto userDto = userService.getUserDto(principal.getId());

        return ResponseEntity.ok(userDto);
    }


    @PatchMapping
    public ResponseEntity<UserDto> updateProfile(
            @RequestBody UpdateUserRequest body,
            @AuthenticationPrincipal CustomPrincipal principal
    ) {
        UserDto userDto = userService.updateUser(body, principal.getId());

        return ResponseEntity.ok(userDto);
    }

}
