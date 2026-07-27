package com.training.newsapi.common.exception;

public abstract class UnauthorizedException extends RuntimeException {

  protected UnauthorizedException(String message) {
    super(message);
  }
}
