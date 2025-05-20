package com.authservice.controller.advice;

import com.authservice.controller.AuthController;
import com.authservice.dto.response.ErrorResponseDto;
import com.authservice.exceptions.EntityAlreadyExistsException;
import com.authservice.exceptions.TokenValidationException;
import com.fasterxml.jackson.core.JsonProcessingException;
import io.grpc.Status;
import io.grpc.StatusRuntimeException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.HashMap;
import java.util.Map;

@RestControllerAdvice(assignableTypes = {AuthController.class})
public class AuthControllerAdvice {


    @ExceptionHandler(JsonProcessingException.class)
    public ResponseEntity<ErrorResponseDto> handleJsonProcessingException(JsonProcessingException e) {
        return ResponseEntity.badRequest().body(new ErrorResponseDto(
                HttpStatus.INTERNAL_SERVER_ERROR.getReasonPhrase(),
                HttpStatus.INTERNAL_SERVER_ERROR.value(),
                e.getMessage()
        ));
    }


    @ExceptionHandler(EntityAlreadyExistsException.class)
    public ResponseEntity<ErrorResponseDto> handleRegisterException(EntityAlreadyExistsException e) {
        return ResponseEntity.badRequest().body(new ErrorResponseDto(
                HttpStatus.CONFLICT.getReasonPhrase(),
                HttpStatus.CONFLICT.value(),
                e.getMessage()
        ));
    }


    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String, String>> handleNotValidMethodArguments(MethodArgumentNotValidException e) {
        Map<String, String> errors = new HashMap<>();
        e.getBindingResult().getFieldErrors().forEach(error -> {
            errors.put(error.getField(), error.getDefaultMessage());
        });
        return ResponseEntity.badRequest().body(errors);
    }


    @ExceptionHandler(TokenValidationException.class)
    public ResponseEntity<ErrorResponseDto> handleTokenValidationException(TokenValidationException e) {
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(new ErrorResponseDto(
                HttpStatus.UNAUTHORIZED.getReasonPhrase(),
                HttpStatus.UNAUTHORIZED.value(),
                e.getMessage()
        ));
    }
}
