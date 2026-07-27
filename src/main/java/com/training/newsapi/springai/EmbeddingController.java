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

/**
 * REST controller for managing document embeddings.
 * Use Swagger UI to ingest content and verify stored embeddings.
 */
@RestController
@RequestMapping("/api/v1/ai/embeddings")
public class EmbeddingController {

  private final EmbeddingService embeddingService;

  public EmbeddingController(EmbeddingService embeddingService) {
    this.embeddingService = embeddingService;
  }

  /**
   * Ingest a single document — generates its embedding and stores it.
   */
  @PostMapping
  public Map<String, String> addDocument(@Valid @RequestBody EmbeddingRequest request) {
    String documentId = embeddingService.addDocument(request.content(), request.metadata());
    return Map.of("documentId", documentId, "status", "embedded");
  }

  /**
   * Ingest multiple documents in a single batch.
   */
  @PostMapping("/batch")
  public Map<String, Object> addDocuments(@Valid @RequestBody List<EmbeddingRequest> requests) {
    List<String> documentIds = embeddingService.addDocuments(requests);
    return Map.of("documentIds", documentIds, "count", documentIds.size(), "status", "embedded");
  }

  /**
   * Search for similar documents — useful for debugging/testing.
   */
  @GetMapping("/search")
  public List<Document> searchDocuments(
      @RequestParam String query,
      @RequestParam(defaultValue = "5") int topK) {
    return embeddingService.searchDocuments(query, topK);
  }
}
