package com.training.newsapi.springai;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import org.springframework.ai.document.Document;
import org.springframework.ai.reader.JsonReader;
import org.springframework.ai.transformer.splitter.TokenTextSplitter;
import org.springframework.ai.vectorstore.SearchRequest;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
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
    TokenTextSplitter splitter = TokenTextSplitter.builder().build();
    vectorStore.add(splitter.split(document));
    return document.getId();
  }

  public List<Document> searchDocuments(String query, int topK) {
    SearchRequest searchRequest = SearchRequest.builder()
        .query(query)
        .topK(topK)
        .build();
    return vectorStore.similaritySearch(searchRequest);
  }

  public int loadPhonesDataInVectorStore() {
    try {
      PathMatchingResourcePatternResolver resolver = new PathMatchingResourcePatternResolver();
      Resource[] resources = resolver.getResources("classpath:phones/*.json");

      List<String> loadedFiles = new ArrayList<>();
      int loadedCount = 0;

      for (Resource resource : resources) {
        if (resource.isReadable()) {
          JsonReader jsonReader = new JsonReader(resource);
          List<Document> documents = jsonReader.read();
          vectorStore.add(documents);
          loadedFiles.add(resource.getFilename());
          loadedCount++;
        }
      }

      return loadedCount;
    } catch (Exception e) {
      System.out.print(e);
      return 0;
    }
  }
}
