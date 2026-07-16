package com.training.lecture02.news;

import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api/v1/news")
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
        Optional<News> value = newsService.findOne(newsId);
        if (value.isPresent()) {
            return ResponseEntity.ok(value.get());
        }
        return ResponseEntity.notFound().build();
    }

    @PreAuthorize("hasAnyRole('EDITOR','REPORTER')")
    @PostMapping
    public ResponseEntity<News> create(@RequestHeader(name = "X-XSRF-TOKEN", required = false) String csrf,
                                       @Valid @RequestBody News news, Authentication auth) {
        news.setReportedBy(auth.getName());
        return ResponseEntity.status(HttpStatus.CREATED).body(newsService.create(news));
    }

    @PreAuthorize("hasAnyRole('EDITOR','REPORTER')")
    @PutMapping("/{newsId}")
    public ResponseEntity<News> update(@RequestHeader(name = "X-XSRF-TOKEN", required = false) String csrf,
                                       @PathVariable int newsId, @Valid @RequestBody News news, Authentication auth) {
        Optional<News> value = newsService.findOne(newsId);
        if (value.isPresent()) {
            if (auth.getName().equals(value.get().getReportedBy()) || hasRole(auth,"EDITOR")) {
                return ResponseEntity.ok(newsService.update(newsId, news));
            }
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }
        return ResponseEntity.notFound().build();
    }

    @PreAuthorize("hasAnyRole('EDITOR')")
    @DeleteMapping("/{newsId}")
    public ResponseEntity<Void> delete(@RequestHeader(name = "X-XSRF-TOKEN", required = false) String csrf,
                                       @PathVariable int newsId) {
        Optional<News> value = newsService.findOne(newsId);
        if (value.isPresent()) {
            newsService.delete(newsId);
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.notFound().build();
    }

    private boolean hasRole(Authentication auth, String role) {
        String target = "ROLE_" + role;
        return auth.getAuthorities().stream()
                .anyMatch(authority -> authority.getAuthority().equals(target));
    }
}