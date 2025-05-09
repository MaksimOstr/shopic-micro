package com.authservice.controller;

import com.authservice.dto.request.SignUpRequestDto;
import com.authservice.services.AuthService;
import com.shopic.grpc.userservice.CreateUserResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    @PostMapping("/register")

    public ResponseEntity<?> register(
            @Valid @RequestBody SignUpRequestDto body
    ) {
        authService.register(body);
        return ResponseEntity.ok().build();
    }
}
