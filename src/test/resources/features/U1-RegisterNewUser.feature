Feature: U1 - As Sarah, I want to register on Gardener’s Grove so that I can use its awesome features.

  @U1
  Scenario: #AC1
    Given I connect to the system’s main URL
    When I see the home page
    Then It includes a form for registering a new user

  @U1
  Scenario: #AC2
    Given I am on the registration form
    And I enter valid values for all fields
    When I submit the register form
    Then I am automatically logged in to my new account
    And I see my user profile page