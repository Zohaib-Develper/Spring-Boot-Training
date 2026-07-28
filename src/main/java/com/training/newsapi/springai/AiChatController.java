package com.training.newsapi.springai;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.memory.ChatMemory;
import org.springframework.ai.tool.annotation.Tool;
import org.springframework.ai.tool.annotation.ToolParam;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/ai")
public class AiChatController {

  private final ChatClient chatClient;

  public AiChatController(ChatClient chatClient) {
    this.chatClient = chatClient;
  }

  @GetMapping("/chat")
  public String chat(@RequestParam String message,
      @RequestParam(defaultValue = "default") String conversationId) {
    return chatClient.prompt()
        .tools(new WeatherTool())
        .user(message)
        .advisors(a -> a.param(ChatMemory.CONVERSATION_ID, conversationId))
        .call()
        .content();
  }

  public static class WeatherTool {

    private final org.springframework.web.client.RestClient restClient = org.springframework.web.client.RestClient.create();

    @Tool(description = "Get the current weather for a location")
    public String getCurrentWeather(@ToolParam(description = "Location name") String location) {
      return "The weather in " + location + " is 49 degree";
    }
  }
}
