package com.training.newsapi.springai;

import jakarta.validation.constraints.NotBlank;
import java.util.Map;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class EmbeddingDTO {

  @NotBlank
  private String content;
  private Map<String, Object> metadata;
}