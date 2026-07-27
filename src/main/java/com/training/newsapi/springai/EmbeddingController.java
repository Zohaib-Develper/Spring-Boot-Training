package com.training.newsapi.springai;

import jakarta.validation.Valid;
import java.util.List;
import java.util.Map;
import org.springframework.ai.document.Document;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/ai/embeddings")
public class EmbeddingController {

  private final EmbeddingService embeddingService;

  public EmbeddingController(EmbeddingService embeddingService) {
    this.embeddingService = embeddingService;
  }

  @PostMapping
  public Map<String, String> addDocument(@Valid @RequestBody EmbeddingDTO dto) {
    String documentId = embeddingService.addDocument(dto.getContent(), dto.getMetadata());
    return Map.of("documentId", documentId, "status", "embedded");
  }

  @GetMapping("/search")
  public List<Document> searchDocuments(
      @RequestParam String query,
      @RequestParam(defaultValue = "5") int topK) {
    return embeddingService.searchDocuments(query, topK);
  }
}
