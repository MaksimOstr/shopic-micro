package com.userservice.controller.advice;

import com.userservice.dto.response.ErrorResponseDto;
import com.userservice.exceptions.EmailVerifyException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class VerificationControllerAdvice {

    @ExceptionHandler(EmailVerifyException.class)
    public ResponseEntity<ErrorResponseDto> emailVerifyException(EmailVerifyException e) {
        return ResponseEntity.badRequest().body(new ErrorResponseDto(
                HttpStatus.BAD_REQUEST.getReasonPhrase(),
                HttpStatus.BAD_REQUEST.value(),
                e.getMessage()
        ));
    }
}
