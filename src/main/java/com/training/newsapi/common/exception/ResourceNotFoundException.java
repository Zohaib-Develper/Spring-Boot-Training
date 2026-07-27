package com.training.newsapi.common.exception;

public abstract class ResourceNotFoundException extends RuntimeException {

  protected ResourceNotFoundException(String message) {
    super(message);
  }
}
