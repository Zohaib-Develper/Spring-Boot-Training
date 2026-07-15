package com.training.lecture02.news;

public class NewsNotFoundException extends RuntimeException {
    public NewsNotFoundException(int id) {
        super("News not found with id: " + id);
    }
}