package com.training.lecture02.news;

import java.time.LocalDateTime;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

@Service
public class NewsService {

    private final NewsRepository newsRepository;

    public NewsService(NewsRepository newsRepository) {
        this.newsRepository = newsRepository;
    }

    public Page<News> findAll(int page, int size) {
        if (page < 0) {
            page = 0;
        }
        if (size <= 0 || size > 100) {
            size = 100;
        }
        return newsRepository.findAll(PageRequest.of(page, size));
    }

    public Optional<News> findOne(int newsId) {
        return newsRepository.findById(newsId);
    }

    public News create(News news) {
        news.setReportedAt(LocalDateTime.now());
        return newsRepository.save(news);
    }

    public News update(int newsId, News news) {
        News existing = newsRepository.findById(newsId).orElseThrow();
        existing.setTitle(news.getTitle());
        existing.setDetails(news.getDetails());
        existing.setReportedAt(LocalDateTime.now());
        return newsRepository.save(existing);
    }

    public void delete(int newsId) {
        newsRepository.deleteById(newsId);
    }
}