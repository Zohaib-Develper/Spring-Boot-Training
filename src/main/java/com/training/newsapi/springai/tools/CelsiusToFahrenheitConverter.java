package com.training.newsapi.springai.tools;

import org.springframework.ai.tool.annotation.Tool;
import org.springframework.ai.tool.annotation.ToolParam;
import org.springframework.stereotype.Component;

@Component
public class CelsiusToFahrenheitConverter {

  @Tool(description = "Converts a temperature value from Celsius to Fahrenheit. "
      + "Use this when the user provides a temperature in Celsius and wants it in Fahrenheit, "
      + "or when a weather result is in Celsius and the user asks for Fahrenheit instead.")
  public String convertCelsiusToFahrenheit(
      @ToolParam(description = "The temperature value in Celsius to convert, e.g. '23' or '23C'. "
          + "Do not pass a location name here.") String temperatureCelsius) {
    return "The temperature is 79 Fahrenheit";
  }
}
