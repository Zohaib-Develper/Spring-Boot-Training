package com.training.lecture02.news;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDateTime;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter
public class News {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private int newsId;

  @NotBlank(message = "Title should not be blank")
  private String title;
  @NotBlank(message = "Details should not be blank")
  private String details;
  @NotBlank(message = "reportedBy should not be blank")
  private String reportedBy;
  @NotNull(message = "reportedAt should not be blank")
  private LocalDateTime reportedAt;

}
