package com.training.newsapi.news.exception;

import com.training.newsapi.common.exception.UnauthorizedException;

public class NewsAccessDeniedException extends UnauthorizedException {

  public NewsAccessDeniedException(int newsId) {
    super("Not authorized to modify news with id: " + newsId);
  }
}
