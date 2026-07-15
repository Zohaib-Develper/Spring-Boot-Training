package com.training.lecture02.news;

import jakarta.transaction.Transactional;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;

import org.springframework.data.domain.Pageable;

@Service
public class NewsService {
    private final NewsRepository newsRepository;

    NewsService(NewsRepository newsRepository){
        this.newsRepository = newsRepository;
    }

    public Page<News> getAllNews(Pageable pageable){
        return newsRepository.findAll(pageable);
    }

    public News getNewsById(int id){
        return newsRepository.findById(id).orElseThrow(() -> new NewsNotFoundException(id));
    }

    public News deleteNews(int id) {
        News news = newsRepository.findById(id)
                .orElseThrow(() -> new NewsNotFoundException(id));
        newsRepository.deleteById(id);
        return news;
    }

    public News createNews(News news){
        return newsRepository.save(news);
    }

    @Transactional
    public News updateNews(int id,News news){
        News existing = newsRepository.findById(id).orElseThrow(() -> new NewsNotFoundException(id));
        existing.setTitle(news.getTitle());
        existing.setDetails(news.getDetails());
        existing.setReportedAt(news.getReportedAt());
        existing.setReportedBy(news.getReportedBy());

        return newsRepository.save(existing);
    }
}
