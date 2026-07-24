package com.training.lecture02.news;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.time.LocalDateTime;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.result.MockMvcResultHandlers;
import tools.jackson.databind.ObjectMapper;

@SpringBootTest
@AutoConfigureMockMvc
@WithMockUser(username = "editor", authorities = {"EDITOR"})
public class NewsIntegrationTest {

  @Autowired
  private MockMvc mockMvc;

  @Autowired
  private ObjectMapper objectMapper;

  @Test
  void getAllNews_shouldReturnSeedsNews() throws Exception {
    mockMvc.perform(get("/api/v1/news"))
        .andDo(MockMvcResultHandlers.print())
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.content[0].title").isString())
        .andExpect(jsonPath("$.content[0].details").isString())
        .andExpect(jsonPath("$.content[0].reportedAt").value(Matchers.notNullValue()))
        .andExpect(jsonPath("$.content[0].reportedBy").value(Matchers.notNullValue()));
  }

  @Test
  void getNewsById_shouldReturnSingleNews() throws Exception {
    mockMvc.perform(get("/api/v1/news/1"))
        .andDo(MockMvcResultHandlers.print())
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.title").isString())
        .andExpect(jsonPath("$.details").isString())
        .andExpect(jsonPath("$.reportedAt").value(Matchers.notNullValue()))
        .andExpect(jsonPath("$.reportedBy").value(Matchers.notNullValue()));
  }

  @Test
  void getNewsById_shouldReturn404_whenNotFound() throws Exception {
    mockMvc.perform(get("/api/v1/news/999999"))
        .andExpect(status().isNotFound());
  }

  @Test
  void createNews_shouldPersistAndReturnNews_whenValid() throws Exception {
    News newNews = new News();
    newNews.setTitle("New Title");
    newNews.setDetails("New Details");
    newNews.setReportedBy("Author B");
    newNews.setReportedAt(LocalDateTime.now());

    mockMvc.perform(post("/api/v1/news")
            .with(csrf())
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(newNews)))
        .andExpect(status().isCreated())
        .andExpect(jsonPath("$.newsId").exists())
        .andExpect(jsonPath("$.title").value("New Title"));
  }

  @Test
  void createNews_shouldReturn400_whenTitleBlank() throws Exception {
    News invalidNews = new News();
    invalidNews.setTitle("");
    invalidNews.setDetails("Details");
    invalidNews.setReportedBy("Author B");
    invalidNews.setReportedAt(LocalDateTime.now());

    mockMvc.perform(post("/api/v1/news")
            .with(csrf())
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(invalidNews)))
        .andDo(MockMvcResultHandlers.print())
        .andExpect(status().isBadRequest())
        .andExpect(jsonPath("$.title").value("Title should not be blank"));
  }

  @Test
  void updateNews_shouldModifyExistingNews() throws Exception {

    News updated = new News();
    updated.setTitle("Updated Title");
    updated.setDetails("Updated Details");
    updated.setReportedBy("Author A");
    updated.setReportedAt(LocalDateTime.now());

    mockMvc.perform(put("/api/v1/news/" + 2)
            .with(csrf())
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(updated)))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.title").value("Updated Title"));
  }

  @Test
  void updateNews_shouldReturn404_whenNewsDoesNotExist() throws Exception {
    News updated = new News();
    updated.setTitle("Updated Title");
    updated.setDetails("Updated Details");
    updated.setReportedBy("Author A");
    updated.setReportedAt(LocalDateTime.now());

    mockMvc.perform(put("/api/v1/news/999999")
            .with(csrf())
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(updated)))
        .andExpect(status().isNotFound());
  }

  @Test
  void deleteNews_shouldRemoveNews() throws Exception {

    mockMvc.perform(delete("/api/v1/news/1")
            .with(csrf()))
        .andExpect(status().is2xxSuccessful());

    mockMvc.perform(get("/api/v1/news/1"))
        .andExpect(status().isNotFound());
  }
}
