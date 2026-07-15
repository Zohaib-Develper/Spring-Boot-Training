package com.training.lecture02.news;

import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.web.PageableDefault;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.data.domain.Pageable;

@RestController
@RequestMapping("/api/v1/news")
public class NewsController {

    private final NewsService newsService;

    NewsController(NewsService newsService){
        this.newsService = newsService;
    }

    @GetMapping("")
    Page<News> getAllNews(@PageableDefault(size = 10, page = 0) Pageable pageable){
        return newsService.getAllNews(pageable);
    }

    @GetMapping("/{id}")
    News getNewsById(@PathVariable int id){
        return newsService.getNewsById(id);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping("/{id}")
    News deleteNews(@PathVariable int id){
        return newsService.deleteNews(id);
    }

    @PostMapping("")
    News createNews(@Valid @RequestBody News news){
        return newsService.createNews(news);
    }

    @PutMapping("/{id}")
    News updateNews(@PathVariable int id, @Valid @RequestBody News news){
        return newsService.updateNews(id, news);
    }
}