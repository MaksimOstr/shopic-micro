package com.userservice.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.userservice.dto.request.EmailVerifyRequestDto;
import com.userservice.dto.request.VerifyUserRequestDto;
import com.userservice.services.UserVerificationService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;


@RestController
@RequestMapping("/users")
@RequiredArgsConstructor
public class UserController {
    private final UserVerificationService userVerificationService;
}