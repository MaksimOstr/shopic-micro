package com.userservice.controller;

import com.userservice.dto.request.CreateUserRequestDto;
import com.userservice.dto.response.CreateUserResponseDto;
import com.userservice.services.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @PostMapping
    public ResponseEntity<?> createUser(
            @Valid @RequestBody CreateUserRequestDto body
    ) {
        CreateUserResponseDto response = userService.createLocalUser(body);
        return ResponseEntity.ok().build();
    }
}
