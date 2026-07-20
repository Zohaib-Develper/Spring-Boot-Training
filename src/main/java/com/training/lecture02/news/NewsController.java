package com.training.lecture02.news;

import io.swagger.v3.oas.annotations.enums.SecuritySchemeType;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.security.SecurityScheme;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

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
    public ResponseEntity<Map<String, Object>> findAll(@RequestParam(required = false, defaultValue = "0") int page,
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
    public ResponseEntity<News> findOne(@PathVariable int newsId) {
        return ResponseEntity.ok(newsService.findOne(newsId));
    }

    @PreAuthorize("hasAnyRole('EDITOR','REPORTER')")
    @PostMapping
    public ResponseEntity<News> create(@RequestHeader(name = "X-XSRF-TOKEN", required = false) String csrf,
                                       @Valid @RequestBody News news) {
        return ResponseEntity.status(HttpStatus.CREATED).body(newsService.create(news));
    }

    @PreAuthorize("hasAnyRole('EDITOR','REPORTER')")
    @PutMapping("/{newsId}")
    public ResponseEntity<News> update(@RequestHeader(name = "X-XSRF-TOKEN", required = false) String csrf,
                                       @PathVariable int newsId, @Valid @RequestBody News news) {
        return ResponseEntity.ok(newsService.update(newsId, news));
    }

    @PreAuthorize("hasAnyRole('EDITOR')")
    @DeleteMapping("/{newsId}")
    public ResponseEntity<Void> delete(@RequestHeader(name = "X-XSRF-TOKEN", required = false) String csrf,
                                       @PathVariable int newsId) {
        newsService.delete(newsId);
        return ResponseEntity.noContent().build();
    }
}