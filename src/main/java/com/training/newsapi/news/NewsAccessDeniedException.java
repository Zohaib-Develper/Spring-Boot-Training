package com.training.newsapi.news;

public class NewsAccessDeniedException extends RuntimeException {

  public NewsAccessDeniedException(int newsId) {
    super("Not authorized to modify news with id: " + newsId);
  }
}