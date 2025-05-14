package com.userservice.exceptions;

public class EmailVerifyException extends RuntimeException {
  public EmailVerifyException(String message) {
    super(message);
  }
}
