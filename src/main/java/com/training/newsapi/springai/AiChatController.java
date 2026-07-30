package com.training.newsapi.springai;

import com.training.newsapi.springai.tools.CelsiusToFahrenheitConverter;
import com.training.newsapi.springai.tools.WeatherTool;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.memory.ChatMemory;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@Validated
@RestController
@RequestMapping("/api/v1/ai")
public class AiChatController {

  private final ChatClient chatClient;
  private final WeatherTool weatherTool;
  private final CelsiusToFahrenheitConverter temperatureConverter;

  public AiChatController(ChatClient chatClient, WeatherTool weatherTool,
      CelsiusToFahrenheitConverter temperatureConverter) {
    this.chatClient = chatClient;
    this.weatherTool = weatherTool;
    this.temperatureConverter = temperatureConverter;
  }

  @GetMapping("/chat")
  public String chat(
      @RequestParam
      @NotBlank(message = "Message must not be blank")
      @Size(max = 2000, message = "Message must not exceed 2000 characters")
      String message,
      @RequestParam(defaultValue = "default")
      @Size(max = 100, message = "Conversation ID must not exceed 100 characters")
      @Pattern(regexp = "^[a-zA-Z0-9_-]+$",
          message = "Conversation ID must only contain alphanumeric characters, hyphens or underscores")
      String conversationId) {
    return chatClient.prompt()
        .tools(weatherTool, temperatureConverter)
        .user(message)
        .advisors(a -> a.param(ChatMemory.CONVERSATION_ID, conversationId))
        .call()
        .content();
  }
}
