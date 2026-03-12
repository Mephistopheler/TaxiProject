Feature: Trip API
  Scenario: Check trip existence
    Given trip service returns true for id "trip-1"
    When e2e client checks trip existence for "trip-1"
    Then trip e2e status is 200