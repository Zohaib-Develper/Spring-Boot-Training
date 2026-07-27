package com.training.newsapi.springai;

import java.util.Collections;
import java.util.List;
import org.springframework.ai.document.Document;
import org.springframework.ai.vectorstore.SearchRequest;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.stereotype.Service;

/**
 * Service for managing document embeddings in the vector store.
 */
@Service
public class EmbeddingService {

  private final VectorStore vectorStore;

  public EmbeddingService(VectorStore vectorStore) {
    this.vectorStore = vectorStore;
  }

  /**
   * Creates a document from the given content and metadata, then stores it
   * in the vector store. The embedding is generated automatically by the
   * configured EmbeddingModel.
   */
  public String addDocument(String content, java.util.Map<String, Object> metadata) {
    var docMetadata = metadata != null ? metadata : Collections.<String, Object>emptyMap();
    Document document = new Document(content, docMetadata);
    vectorStore.add(List.of(document));
    return document.getId();
  }

  /**
   * Adds multiple documents to the vector store in a single batch.
   */
  public List<String> addDocuments(List<EmbeddingRequest> requests) {
    List<Document> documents = requests.stream()
        .map(req -> new Document(
            req.content(),
            req.metadata() != null ? req.metadata() : Collections.<String, Object>emptyMap()))
        .toList();
    vectorStore.add(documents);
    return documents.stream().map(Document::getId).toList();
  }

  /**
   * Performs a similarity search against the vector store.
   * Useful for debugging and verifying that embeddings are stored correctly.
   */
  public List<Document> searchDocuments(String query, int topK) {
    SearchRequest searchRequest = SearchRequest.builder()
        .query(query)
        .topK(topK)
        .build();
    return vectorStore.similaritySearch(searchRequest);
  }
}
