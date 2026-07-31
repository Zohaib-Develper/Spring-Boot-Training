package com.training.newsapi.cucumber.steps;

import static org.junit.jupiter.api.Assertions.assertEquals;

import io.cucumber.java.en.And;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import java.time.Duration;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;

public class SwaggerUiStepDefs {

  private WebDriver driver;

  @Given("I open a browser")
  public void iOpenABrowser() {
    ChromeOptions options = new ChromeOptions();
    options.addArguments("--start-maximized");
    driver = new ChromeDriver(options);
    driver.manage().timeouts().pageLoadTimeout(Duration.ofSeconds(30));
    driver.manage().timeouts().implicitlyWait(Duration.ofSeconds(10));
  }

  @When("I navigate to {string}")
  public void iNavigateTo(String url) {
    driver.get(url);
  }

  @Then("the page title should be {string}")
  public void thePageTitleShouldBe(String expectedTitle) {
    assertEquals(expectedTitle, driver.getTitle(),
        "Page title does not match expected value");
  }

  @And("I close the browser")
  public void iCloseTheBrowser() {
    if (driver != null) {
      driver.quit();
    }
  }
}
