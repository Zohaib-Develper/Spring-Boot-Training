package com.training.lecture02.news;

import java.time.LocalDateTime;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.stereotype.Service;

import com.training.lecture02.security.Role;

@Service
public class NewsService {

    private static final int MAX_PAGE_SIZE = 100;
    private static final int DEFAULT_PAGE_SIZE = 10;

    private final NewsRepository newsRepository;

    public NewsService(NewsRepository newsRepository) {
        this.newsRepository = newsRepository;
    }

    public Page<News> findAll(int page, int size) {
        int safePage = Math.max(page, 0);
        int safeSize = (size <= 0 || size > MAX_PAGE_SIZE) ? DEFAULT_PAGE_SIZE : size;
        return newsRepository.findAll(PageRequest.of(safePage, safeSize));
    }

    public News findOne(int newsId) {
        return newsRepository.findById(newsId)
                .orElseThrow(() -> new NewsNotFoundException(newsId));
    }

    public News create(News news, Authentication auth) {
        news.setReportedBy(auth.getName());
        news.setReportedAt(LocalDateTime.now());
        return newsRepository.save(news);
    }

    public News update(int newsId, News news, Authentication auth) {
        News existing = newsRepository.findById(newsId)
                .orElseThrow(() -> new NewsNotFoundException(newsId));

        if (!isOwner(auth, existing) && !hasRole(auth, Role.EDITOR)) {
            throw new NewsAccessDeniedException(newsId);
        }

        existing.setTitle(news.getTitle());
        existing.setDetails(news.getDetails());
        existing.setReportedAt(LocalDateTime.now());
        return newsRepository.save(existing);
    }

    public void delete(int newsId) {
        if (!newsRepository.existsById(newsId)) {
            throw new NewsNotFoundException(newsId);
        }
        newsRepository.deleteById(newsId);
    }

    private boolean isOwner(Authentication auth, News news) {
        return auth.getName().equals(news.getReportedBy());
    }

    private boolean hasRole(Authentication auth, Role role) {
        String target = "ROLE_" + role.name();
        return auth.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .anyMatch(authority -> authority.equals(target));
    }
}