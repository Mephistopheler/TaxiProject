Feature: Passenger API
  Scenario: Exists endpoint returns true
    Given passenger with id 77 exists
    When e2e client checks passenger existence for id 77
    Then passenger e2e status is 200