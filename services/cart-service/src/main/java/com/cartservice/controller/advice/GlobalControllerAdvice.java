package com.cartservice.controller.advice;

import com.cartservice.dto.response.ErrorResponseDto;
import com.cartservice.exception.ApiException;
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


    @ExceptionHandler(ApiException.class)
    public ResponseEntity<ErrorResponseDto> handleApiException(ApiException e) {
        return ResponseEntity.status(e.getStatus()).body(new ErrorResponseDto(
                e.getStatus().getReasonPhrase(),
                e.getStatus().value(),
                e.getMessage()
        ));
    }
}
