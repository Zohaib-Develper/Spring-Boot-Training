package com.training.newsapi.cucumber.steps;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import io.cucumber.java.en.And;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;

public class WelcomeStepDefs {

  @Autowired
  private MockMvc mockMvc;

  private ResultActions result;

  @Given("the application is running")
  public void theApplicationIsRunning() {
    // Spring Boot context is already started by @SpringBootTest — nothing extra needed.
  }

  @When("I send a GET request to {string}")
  public void iSendAGetRequestTo(String path) throws Exception {
    result = mockMvc.perform(get(path));
  }

  @Then("the response status should be {int}")
  public void theResponseStatusShouldBe(int expectedStatus) throws Exception {
    switch (expectedStatus) {
      case 200 -> result.andExpect(status().isOk());
      case 201 -> result.andExpect(status().isCreated());
      case 400 -> result.andExpect(status().isBadRequest());
      case 401 -> result.andExpect(status().isUnauthorized());
      case 403 -> result.andExpect(status().isForbidden());
      case 404 -> result.andExpect(status().isNotFound());
      default -> result.andExpect(status().is(expectedStatus));
    }
  }

  @And("the response body should be {string}")
  public void theResponseBodyShouldBe(String expectedBody) throws Exception {
    result.andExpect(content().string(expectedBody));
  }
}
