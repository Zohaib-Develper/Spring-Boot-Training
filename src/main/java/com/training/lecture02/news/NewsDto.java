package com.training.lecture02.news;

import java.time.LocalDateTime;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class NewsDto {

  private int newsId;
  private String title;
  private String details;
  private String reportedBy;
  private LocalDateTime reportedAt;

  public static NewsDto from(News news) {
    NewsDto dto = new NewsDto();
    dto.setDetails(news.getDetails());
    dto.setTitle(news.getTitle());
    dto.setReportedAt(news.getReportedAt());
    dto.setReportedBy(news.getReportedBy());
    dto.setNewsId(news.getNewsId());
    return dto;
  }
}
