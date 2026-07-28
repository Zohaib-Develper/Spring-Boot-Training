package com.training.newsapi.springai.tools;

import org.springframework.ai.tool.annotation.Tool;
import org.springframework.ai.tool.annotation.ToolParam;
import org.springframework.stereotype.Component;

@Component
public class WeatherTool {

  @Tool(description =
      "Gets the current weather conditions and temperature (in Celsius) for a given city or location. "
          + "Use this whenever the user asks about current weather, temperature, or conditions in a specific place. "
          + "Do not use this for temperature unit conversions.")
  public String getCurrentWeather(
      @ToolParam(description =
          "The name of the city or location to get weather for, e.g. 'London' or 'New York'. "
              + "Do not pass a temperature value here.") String location) {
    return "The weather in " + location + " is 49 degree and sunny";
  }
}
