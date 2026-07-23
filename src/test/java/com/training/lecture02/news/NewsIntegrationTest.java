package com.training.lecture02.news;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDateTime;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.result.MockMvcResultHandlers;
import tools.jackson.databind.ObjectMapper;

@SpringBootTest(
    properties = {
        "spring.security.oauth2.client.registration.google.client-id=dummy",
        "spring.security.oauth2.client.registration.google.client-secret=dummy",
        "spring.security.oauth2.client.registration.github.client-id=dummy",
        "spring.security.oauth2.client.registration.github.client-secret=dummy"
    }
)
@AutoConfigureMockMvc
public class NewsIntegrationTest {

  @Autowired
  private MockMvc mockMvc;

  @Autowired
  private ObjectMapper objectMapper;

  @DynamicPropertySource
  static void jwtProperties(DynamicPropertyRegistry registry) throws IOException {
    String privateKey = Files.readString(Path.of("src/test/resources/private_key.pem"));
    String publicKey = Files.readString(Path.of("src/test/resources/public_key.pem"));
    registry.add("jwt.private-key", () -> privateKey);
    registry.add("jwt.public-key", () -> publicKey);
  }

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
    mockMvc.perform(get("/api/v1/news/2"))
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
  @WithMockUser(username = "editor", authorities = {"EDITOR", "ROLE_EDITOR"})
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
  @WithMockUser(username = "editor", authorities = {"EDITOR", "ROLE_EDITOR"})
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
  @WithMockUser(username = "reporter", authorities = {"REPORTER", "ROLE_REPORTER"})
  void updateNews_shouldModifyExistingNews() throws Exception {

    News updated = new News();
    updated.setTitle("Updated Title");
    updated.setDetails("Updated Details");
    updated.setReportedBy("reporter");
    updated.setReportedAt(LocalDateTime.now());

    mockMvc.perform(put("/api/v1/news/" + 1)
            .with(csrf())
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(updated)))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.title").value("Updated Title"))
        .andExpect(jsonPath("$.reportedBy").value("reporter"));
  }

  @Test
  @WithMockUser(username = "admin", authorities = {"EDITOR", "ROLE_EDITOR"})
  void updateNews_shouldAllowEditorToUpdateOthersNews() throws Exception {
    News updated = new News();
    updated.setTitle("Editor Updated Title");
    updated.setDetails("Editor Updated Details");
    updated.setReportedBy("admin");
    updated.setReportedAt(LocalDateTime.now());

    mockMvc.perform(put("/api/v1/news/" + 2)
            .with(csrf())
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(updated)))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.title").value("Editor Updated Title"))
        .andExpect(jsonPath("$.reportedBy").value("editor"));
  }

  @Test
  @WithMockUser(username = "other-reporter", authorities = {"REPORTER", "ROLE_REPORTER"})
  void updateNews_shouldReturn403_whenReporterUpdatesOthersNews() throws Exception {
    News updated = new News();
    updated.setTitle("Unauthorized Update");
    updated.setDetails("Should fail");
    updated.setReportedBy("other-reporter");
    updated.setReportedAt(LocalDateTime.now());

    mockMvc.perform(put("/api/v1/news/" + 2)
            .with(csrf())
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(updated)))
        .andExpect(status().isForbidden())
        .andExpect(jsonPath("$.error").value("Forbidden"));
  }

  @Test
  @WithMockUser(username = "editor", authorities = {"EDITOR", "ROLE_EDITOR"})
  void updateNews_shouldReturn404_whenNewsDoesNotExist() throws Exception {
    News updated = new News();
    updated.setTitle("Updated Title");
    updated.setDetails("Updated Details");
    updated.setReportedBy("editor");
    updated.setReportedAt(LocalDateTime.now());

    mockMvc.perform(put("/api/v1/news/999999")
            .with(csrf())
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(updated)))
        .andExpect(status().isNotFound());
  }

  @Test
  @WithMockUser(username = "editor", authorities = {"EDITOR", "ROLE_EDITOR"})
  void deleteNews_shouldRemoveNews() throws Exception {

    mockMvc.perform(delete("/api/v1/news/1")
            .with(csrf()))
        .andExpect(status().isNoContent());

    mockMvc.perform(get("/api/v1/news/1"))
        .andExpect(status().isNotFound());
  }
}
