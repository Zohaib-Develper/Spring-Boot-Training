package com.training.newsapi.springai;

import com.training.newsapi.springai.tools.CelsiusToFahrenheitConverter;
import com.training.newsapi.springai.tools.WeatherTool;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.memory.ChatMemory;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

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
  public String chat(@RequestParam String message,
      @RequestParam(defaultValue = "default") String conversationId) {
    return chatClient.prompt()
        .tools(weatherTool, temperatureConverter)
        .user(message)
        .advisors(a -> a.param(ChatMemory.CONVERSATION_ID, conversationId))
        .call()
        .content();
  }
}
