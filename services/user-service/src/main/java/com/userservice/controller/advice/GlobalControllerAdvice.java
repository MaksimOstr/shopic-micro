package com.userservice.controller.advice;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.userservice.dto.response.ErrorResponseDto;
import com.userservice.exceptions.CodeVerificationException;
import com.userservice.exceptions.EmailVerifyException;
import com.userservice.exceptions.EntityDoesNotExistException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.HashMap;
import java.util.Map;

@RestControllerAdvice
public class GlobalControllerAdvice {

    @ExceptionHandler({EmailVerifyException.class, CodeVerificationException.class})
    public ResponseEntity<ErrorResponseDto> handleCodeVerificationException(RuntimeException e) {
        return ResponseEntity.badRequest().body(new ErrorResponseDto(
                HttpStatus.BAD_REQUEST.getReasonPhrase(),
                HttpStatus.BAD_REQUEST.value(),
                e.getMessage()
        ));
    }

    @ExceptionHandler(EntityDoesNotExistException.class)
    public ResponseEntity<ErrorResponseDto> handleEntityDoesNotExistException(EntityDoesNotExistException e) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body((new ErrorResponseDto(
                HttpStatus.NOT_FOUND.getReasonPhrase(),
                HttpStatus.NOT_FOUND.value(),
                e.getMessage()
        )));
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    private ResponseEntity<Map<String, String>> handleNotValidMethodArguments(MethodArgumentNotValidException e) {
        Map<String, String> errors = new HashMap<>();
        e.getBindingResult().getFieldErrors().forEach(error -> {
            errors.put(error.getField(), error.getDefaultMessage());
        });
        return ResponseEntity.badRequest().body(errors);
    }

    @ExceptionHandler(JsonProcessingException.class)
    public ResponseEntity<ErrorResponseDto> handleJsonProcessingException(JsonProcessingException e) {
        return ResponseEntity.badRequest().body(new ErrorResponseDto(
                HttpStatus.INTERNAL_SERVER_ERROR.getReasonPhrase(),
                HttpStatus.INTERNAL_SERVER_ERROR.value(),
                e.getMessage()
        ));
    }
}
