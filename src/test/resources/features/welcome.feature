Feature: Welcome API

  Scenario: Calling the welcome endpoint returns a greeting message
    When I send a GET request to "/api/v1/welcome"
    Then the response status should be 200
    And the response body should be "Hello from WelcomeController"
