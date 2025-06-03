package com.productservice.controller.advice;

import com.productservice.dto.response.ErrorResponseDto;
import com.productservice.exceptions.InvalidEnumArgException;
import com.productservice.exceptions.NotFoundException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import software.amazon.awssdk.awscore.exception.AwsServiceException;
import software.amazon.awssdk.core.exception.SdkClientException;
import software.amazon.awssdk.services.s3.model.S3Exception;

import java.util.HashMap;
import java.util.Map;

@RestControllerAdvice
public class ProductControllerAdvice {
    @ExceptionHandler(MethodArgumentNotValidException.class)
    private ResponseEntity<Map<String, String>> handleNotValidMethodArguments(MethodArgumentNotValidException e) {
        Map<String, String> errors = new HashMap<>();
        e.getBindingResult().getFieldErrors().forEach(error -> {
            errors.put(error.getField(), error.getDefaultMessage());
        });
        return ResponseEntity.badRequest().body(errors);
    }

    @ExceptionHandler(S3Exception.class)
    public ResponseEntity<ErrorResponseDto> handleS3Exception(S3Exception ex) {

        String errorCode = ex.awsErrorDetails().errorCode();
        String message = ex.getMessage();
        HttpStatus status = HttpStatus.valueOf(ex.statusCode());

        ErrorResponseDto error = new ErrorResponseDto(errorCode, status.value(), message);
        return new ResponseEntity<>(error, status);
    }

    @ExceptionHandler(AwsServiceException.class)
    public ResponseEntity<ErrorResponseDto> handleAwsServiceException(AwsServiceException ex) {
        ErrorResponseDto error = new ErrorResponseDto(
                "AWS_SERVICE_ERROR",
                ex.statusCode(),
                "AWS error: " + ex.getMessage()

        );
        return new ResponseEntity<>(error, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @ExceptionHandler(SdkClientException.class)
    public ResponseEntity<ErrorResponseDto> handleSdkClientException(SdkClientException ex) {
        ErrorResponseDto error = new ErrorResponseDto(
                "CLIENT_ERROR",
                HttpStatus.BAD_REQUEST.value(),
                ex.getMessage()
        );
        return new ResponseEntity<>(error, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler({InvalidEnumArgException.class, NotFoundException.class})
    public ResponseEntity<ErrorResponseDto> handleInvalidEnumArgException(RuntimeException e) {
        ErrorResponseDto error = new ErrorResponseDto(
                HttpStatus.BAD_REQUEST.getReasonPhrase(),
                HttpStatus.BAD_REQUEST.value(),
                e.getMessage()
        );

        return ResponseEntity.badRequest().body(error);
    }

}
