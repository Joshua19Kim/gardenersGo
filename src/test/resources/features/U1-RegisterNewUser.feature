Feature: U1 - As Sarah, I want to register on Gardener’s Grove so that I can use its awesome features.

  @U1
  Scenario: #AC1
    Given I connect to the system’s main URL
    When I see the home page
    Then It includes a form for registering a new user

  @U1
  Scenario: #AC2
    Given I am on the registration form
    And I enter the first name "John"
    And I enter the last name "Doe"
    And I enter the email address "a@gmail.com"
    And I enter the password "Password1!"
    And I confirm my password as "Password1!"
    And I enter a date of birth of "01/01/2000"
    When I submit the register form
    Then I am redirected to the signup code page

  @U1
  Scenario: #AC3
    Given I am on the registration form
    And I click the check box marked I have no surname ticked,
    Then the last name text field is disabled
    And it will be ignored when I click the Sign Up button

  @U1
  Scenario: #AC4
    Given I am on the registration form
    And I click the check box marked I have no surname ticked,
    Then the last name text field is disabled
    And it will be ignored when I click the Sign Up button
