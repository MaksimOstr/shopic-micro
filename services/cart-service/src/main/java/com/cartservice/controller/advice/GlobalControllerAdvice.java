package com.cartservice.controller.advice;

import com.cartservice.dto.response.ErrorResponseDto;
import com.cartservice.exception.InsufficientProductStockException;
import com.cartservice.exception.NotFoundException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.HashMap;
import java.util.Map;

@RestControllerAdvice
public class GlobalControllerAdvice {
    @ExceptionHandler(MethodArgumentNotValidException.class)
    private ResponseEntity<Map<String, String>> handleNotValidMethodArguments(MethodArgumentNotValidException e) {
        Map<String, String> errors = new HashMap<>();
        e.getBindingResult().getFieldErrors().forEach(error -> {
            errors.put(error.getField(), error.getDefaultMessage());
        });
        return ResponseEntity.badRequest().body(errors);
    }

    @ExceptionHandler(NotFoundException.class)
    private ResponseEntity<ErrorResponseDto> handleNotFoundException(NotFoundException e) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new ErrorResponseDto(
                HttpStatus.NOT_FOUND.getReasonPhrase(),
                HttpStatus.NOT_FOUND.value(),
                e.getMessage()
        ));
    }

    @ExceptionHandler(InsufficientProductStockException.class)
    private ResponseEntity<ErrorResponseDto> handleAlreadyExists(InsufficientProductStockException e) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ErrorResponseDto(
                HttpStatus.BAD_REQUEST.getReasonPhrase(),
                HttpStatus.BAD_REQUEST.value(),
                e.getMessage()
        ));
    }
}
