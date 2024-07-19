Feature: U1 - As Sarah, I want to register on Gardener’s Grove so that I can use its awesome features.

  @U1
  Scenario: #AC1
    Given I connect to the system’s main URL
    When I see the home page
    Then It includes a button labelled "Not Registered? Create an account"