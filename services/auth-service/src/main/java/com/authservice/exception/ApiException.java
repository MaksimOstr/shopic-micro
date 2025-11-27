package com.authservice.exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public class ApiException extends RuntimeException {
    HttpStatus status;


    public ApiException(String message, HttpStatus status) {
        super(message);
        this.status = status;
    }
}
