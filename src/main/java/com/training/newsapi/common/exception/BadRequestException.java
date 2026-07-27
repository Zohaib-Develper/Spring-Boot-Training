package com.training.newsapi.common.exception;

public abstract class BadRequestException extends RuntimeException {

  protected BadRequestException(String message) {
    super(message);
  }
}
