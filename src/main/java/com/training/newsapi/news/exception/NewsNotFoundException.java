package com.training.newsapi.news.exception;

import com.training.newsapi.common.exception.ResourceNotFoundException;

public class NewsNotFoundException extends ResourceNotFoundException {

  public NewsNotFoundException(int id) {
    super("News not found with id: " + id);
  }
}
