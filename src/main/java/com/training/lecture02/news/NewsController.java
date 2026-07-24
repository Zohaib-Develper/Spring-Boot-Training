package com.training.lecture02.news;

import io.swagger.v3.oas.annotations.enums.SecuritySchemeType;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.security.SecurityScheme;
import jakarta.validation.Valid;
import java.util.Map;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/news")
@SecurityScheme(
    name = "bearerAuth",
    type = SecuritySchemeType.HTTP,
    scheme = "bearer",
    bearerFormat = "JWT"
)
@SecurityRequirement(name = "bearerAuth")
public class NewsController {

  private final NewsService newsService;

  public NewsController(NewsService newsService) {
    this.newsService = newsService;
  }

  @PreAuthorize("permitAll()")
  @GetMapping
  public ResponseEntity<Map<String, Object>> findAll(
      @RequestParam(required = false, defaultValue = "0") int page,
      @RequestParam(required = false, defaultValue = "10") int size) {
    Page<News> news = newsService.findAll(page, size);
    Map<String, Object> body = Map.of(
        "content", news.getContent(),
        "page", news.getNumber(),
        "size", news.getSize());
    return ResponseEntity.ok(body);
  }

  @PreAuthorize("permitAll()")
  @GetMapping("/{newsId}")
  public ResponseEntity<NewsDto> findOne(@PathVariable int newsId) {
    return ResponseEntity.ok(NewsDto.from(newsService.findOne(newsId)));
  }

  @PreAuthorize("hasAnyAuthority('EDITOR','REPORTER')")
  @PostMapping
  public ResponseEntity<NewsDto> create(
      @RequestHeader(name = "X-XSRF-TOKEN", required = false) String csrf,
      @Valid @RequestBody NewsDto dto,
      Authentication auth) {
    return ResponseEntity.status(HttpStatus.CREATED).body(newsService.create(dto, auth));
  }

  @PreAuthorize("hasAnyAuthority('EDITOR','REPORTER')")
  @PutMapping("/{newsId}")
  public ResponseEntity<NewsDto> update(
      @RequestHeader(name = "X-XSRF-TOKEN", required = false) String csrf,
      @PathVariable int newsId, @Valid @RequestBody NewsDto dto,
      Authentication auth) {
    return ResponseEntity.ok(newsService.update(newsId, dto, auth));
  }

  @PreAuthorize("hasAnyAuthority('EDITOR')")
  @DeleteMapping("/{newsId}")
  public ResponseEntity<Void> delete(
      @RequestHeader(name = "X-XSRF-TOKEN", required = false) String csrf,
      @PathVariable int newsId) {
    newsService.delete(newsId);
    return ResponseEntity.noContent().build();
  }

  @GetMapping("/report")
  public ResponseEntity<String> generateReport() {
    newsService.report();
    return ResponseEntity.ok("Reports Generated Successfully");
  }
}