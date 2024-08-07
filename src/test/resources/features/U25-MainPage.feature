Feature: U25 - As Liam, I want to have a main page for the application so that I can easily access other functionality and customize the information I am shown.

  @U25 #this is partially tested, so need to add bit more to satisfy completely
  Scenario: AC1 - Given I am a valid user, when I log in, then I am taken by default to the home page with a summary of my three most recently accessed gardens, three newest plants, my friends list.
    Given I am a valid user
    When I submit the login form
    Then I am taken by default to the home page
    And I can see the empty section for the recently accessed gardens and friends list


  @U25 #this is partially tested, so need to add bit more to satisfy completely
  Scenario: AC1 - Given I am a valid user, when I log in, then I am taken by default to the home page with a summary of my three most recently accessed gardens, three newest plants, my friends list.
    Given I am a valid user
    When I submit the login form
    And I have three friends and visited three different gardens in the past
    Then I am taken by default to the home page
    And I can see the recently accessed gardens and friends on the list