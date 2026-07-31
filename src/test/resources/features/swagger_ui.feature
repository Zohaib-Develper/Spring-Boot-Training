Feature: Swagger UI Page

  Scenario: Swagger UI page should have correct title
    Given I open a browser
    When I navigate to "http://localhost:8080/swagger-ui/index.html"
    Then the page title should be "Swagger UI"
    And I close the browser
