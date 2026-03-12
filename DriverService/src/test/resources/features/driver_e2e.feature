Feature: Driver API
  Scenario: Get driver by id
    Given driver service has driver with id 10
    When e2e client requests driver with id 10
    Then e2e response status is 200