package com.training.lecture02.news;

import com.training.lecture02.security.Role;
import java.time.LocalDateTime;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.scheduling.annotation.Async;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.stereotype.Service;

@Service
@Slf4j
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

  public NewsDto create(NewsDto dto, Authentication auth) {
    News news = new News();
    news.setDetails(dto.getDetails());
    news.setTitle(dto.getTitle());
    news.setReportedBy(auth.getName());
    news.setReportedAt(LocalDateTime.now());
    return NewsDto.from(newsRepository.save(news));
  }

  public NewsDto update(int newsId, NewsDto dto, Authentication auth) {
    News existing = newsRepository.findById(newsId)
        .orElseThrow(() -> new NewsNotFoundException(newsId));

    if (!isOwner(auth, existing) && !hasRole(auth, Role.EDITOR)) {
      throw new NewsAccessDeniedException(newsId);
    }

    existing.setTitle(dto.getTitle());
    existing.setDetails(dto.getDetails());
    existing.setReportedAt(LocalDateTime.now());
    return NewsDto.from(newsRepository.save(existing));
  }

  public void delete(int newsId) {
    if (!newsRepository.existsById(newsId)) {
      throw new NewsNotFoundException(newsId);
    }
    newsRepository.deleteById(newsId);
  }

  @Async
  void report() {
    for (News news : newsRepository.findAll()) {
      log.info("Title: " + news.getTitle());
    }
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