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

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

@RestControllerAdvice
public class ProductControllerAdvice {
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


    @ExceptionHandler(IOException.class)
    public ResponseEntity<ErrorResponseDto> handleException(Exception e) {
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new ErrorResponseDto(
                HttpStatus.INTERNAL_SERVER_ERROR.getReasonPhrase(),
                HttpStatus.INTERNAL_SERVER_ERROR.value(),
                e.getMessage()
        ));
    }

}
