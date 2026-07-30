package com.training.newsapi.news;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import java.time.LocalDateTime;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class NewsDto {

  private int newsId;

  @NotBlank(message = "Title must not be blank")
  @Size(max = 255, message = "Title must not exceed 255 characters")
  private String title;

  @NotBlank(message = "Details must not be blank")
  @Size(max = 1000, message = "Details must not exceed 1000 characters")
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
