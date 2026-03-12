Feature: Rating API
  Scenario: Create rating request
    Given rating service is ready to accept rates
    When e2e client sends a valid rating
    Then rating e2e status is 201