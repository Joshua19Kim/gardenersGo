Feature: U25 - As Liam, I want to have a main page for the application so that I can easily access other functionality and customize the information I am shown.

  @U25
  Scenario: #AC1 - recently added gardens, my friends list with no gardens and friends
    Given I am a valid user
    When I submit the login form
    Then I am taken by default to the home page
    And I can see the empty section for the recently accessed gardens and friends list

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





  @U25
  Scenario: AC1  recently added gardens, my friends list
    Given I am a valid user
    When I submit the login form
    And I have three friends and visited three different gardens in the past
    Then I am taken by default to the home page
    And I can see the recently accessed gardens and friends on the list


  @U25
  Scenario Outline: #AC3 - User can tick(untick) widgets to show/hide
    Given I am a valid user
    When I unticked <listName> on customise main page modal to hide the widget on the main page
    Then I cannot see <listName> List on the main page
    Examples:
      | listName                |
      | "recentlyAccessedGardens" |
      | "newestPlantsList"        |
      | "friendsList"             |
      | "myGardensList"           |

