Feature: U1 - As Sarah, I want to register on Gardener’s Grove so that I can use its awesome features.

  @U1
  Scenario: #AC1
    Given I connect to the system’s main URL
    When I see the home page
    Then It includes a form for registering a new user

  @U1
  Scenario: #AC2
    Given I am on the registration form
    And I enter the first name "John", last name "Doe", email address "a@gmail.com", the password "Password1!" twice, and a date of birth of "01/01/2000"
    When I submit the register form
    Then I am redirected to the signup code page