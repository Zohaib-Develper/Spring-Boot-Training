package com.training.newsapi.springai;

import java.util.Collections;
import java.util.List;
import org.springframework.ai.document.Document;
import org.springframework.ai.vectorstore.SearchRequest;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.stereotype.Service;

@Service
public class EmbeddingService {

  private final VectorStore vectorStore;

  public EmbeddingService(VectorStore vectorStore) {
    this.vectorStore = vectorStore;
  }

  public String addDocument(String content, java.util.Map<String, Object> metadata) {
    var docMetadata = metadata != null ? metadata : Collections.<String, Object>emptyMap();
    Document document = new Document(content, docMetadata);
    vectorStore.add(List.of(document));
    return document.getId();
  }

  public List<Document> searchDocuments(String query, int topK) {
    SearchRequest searchRequest = SearchRequest.builder()
        .query(query)
        .topK(topK)
        .build();
    return vectorStore.similaritySearch(searchRequest);
  }
}
