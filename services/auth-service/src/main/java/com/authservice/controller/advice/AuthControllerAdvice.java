package com.authservice.controller.advice;

import com.authservice.controller.AuthController;
import com.authservice.dto.response.ErrorResponseDto;
import com.authservice.exceptions.RegisterException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice(assignableTypes = {AuthController.class})
public class AuthControllerAdvice {

    @ExceptionHandler(RegisterException.class)
    public ResponseEntity<ErrorResponseDto> handleRegisterException(RegisterException e) {
        return ResponseEntity.badRequest().body(new ErrorResponseDto(
                HttpStatus.BAD_REQUEST.getReasonPhrase(),
                HttpStatus.BAD_REQUEST.value(),
                e.getMessage()
        ));
    }
}
