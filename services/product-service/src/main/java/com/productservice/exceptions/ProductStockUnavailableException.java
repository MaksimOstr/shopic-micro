package com.productservice.exceptions;

public class ProductStockUnavailableException extends RuntimeException {
  public ProductStockUnavailableException(String message) {
    super(message);
  }
}
