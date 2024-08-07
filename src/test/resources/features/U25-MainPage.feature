Feature: U25 - As Liam, I want to have a main page for the application so that I can easily access other functionality and customize the information I am shown.

  @U25
  Scenario: #AC1
    Given I am a valid user
    When I submit the login form
    Then I am taken by default to the home page

  @U25
  Scenario: #AC8 - Newest Plants with no plants
    Given I am a valid user
    When I submit the login form
    Then I am taken by default to the home page
    And I can see the empty widget of newest plants

  @U25
  Scenario: #AC5 - Newest Plant with plants
    Given I am a valid user
    When I submit the login form
    And I have a garden and three plants
    Then I am taken by default to the home page
    And I can see the newest plant widget with my three newest plants


