package com.training.newsapi.springai;

import jakarta.validation.Valid;
import java.util.List;
import java.util.Map;
import org.springframework.ai.document.Document;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
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
  public ResponseEntity<Map<String, String>> addDocument(@Valid @RequestBody EmbeddingDTO dto) {
    String documentId = embeddingService.addDocument(dto.getContent(), dto.getMetadata());
    return ResponseEntity.status(HttpStatus.CREATED).body(Map.of("documentId", documentId));
  }

  @GetMapping("/search")
  public ResponseEntity<List<Document>> searchDocuments(
      @RequestParam String query,
      @RequestParam(defaultValue = "5") int topK) {
    return ResponseEntity.ok(embeddingService.searchDocuments(query, topK));
  }

  @GetMapping("/load-phone-details")
  public ResponseEntity<Map<String, Object>> loadPhonesDataInVectorStore() {
    int loadedFilesCount = embeddingService.loadPhonesDataInVectorStore();
    return ResponseEntity.ok(Map.of("Loaded files count", loadedFilesCount));
  }
}
