package com.training.newsapi.springai;

import jakarta.validation.constraints.NotBlank;
import java.util.Map;

/**
 * DTO for embedding ingestion requests.
 *
 * @param content  the text content to embed and store
 * @param metadata optional key-value metadata attached to the document
 */
public record EmbeddingRequest(
    @NotBlank String content,
    Map<String, Object> metadata
) {
}
