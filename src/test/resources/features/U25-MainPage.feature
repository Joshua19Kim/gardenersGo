Feature: U25 - As Liam, I want to have a main page for the application so that I can easily access other functionality and customize the information I am shown.

  @U25
  Scenario: #AC1
    Given I am a valid user
    When I submit the login form
    Then I am taken by default to the home page

  @U25
  Scenario: #AC5 - Newest Plant
    Given I am a valid user with gardens with plants
    When I submit the login form
    Then I am taken by default to the home page
    And the newest plant widget displays my three newest plants

  @U25
  Scenario: #AC8 - Newest Plant
    Given I am a valid user with gardens with no plants
    When I submit the login form
    Then I am taken by default to the home page
    And the newest plant widget displays error message
