Feature: U7002 - As Jackie Wei, I want to catalog identified plants into my personal collection, so that I can keep track of the plants I've discovered.


  @U7002
  Scenario Outline: #AC3 - Save an identified plant
    Given I am logged in with email "a@gmail.com" and password "Password1!"
    And I have an image of a plant
    When the app identifies the plant image
    And the app displays the name and relevant details after identifying
    And I input a <name> and <description>
    Then my plant is saved
    Examples:
      | name      | description                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                        |
      | "Timothy" | "Apple"                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                            |
      | "Timothy" | "abcabcabcabcabcabcabcabcabcabcabcabcabcabcabcabcabcabcabcabcabcabcabcabcabcabcabcabcabcabcabcabcabcabcabcabcabcabcabcabcabcabcabcabcabcabcabcabcabcabcabcabcabcabcabcabcabcabcabcabcabcabcabcabcabcabcabcabcabcabcabcabcabcabcabcabcabcabcabcabcabcabcabcabcabcabcabcabcabcabcabcabcabcabcabcabcabcabcabcabcabcabcabcabcabcabcabcabcabcabcabcabcabcabcabcabcabcabcabcabcabcabcabcabcabcabcabcabcabcabcabcabcabcabcabcabcabcabcabcabcabcabcabcabcabcabcabcabcabcabcabcabcabcabcabcabcabcabcabcabcabcabcabcabcabcabcabcabcabcabca" |
      | "Timothy" | ""                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                 |


  @U7002
  Scenario Outline: #AC3 - Invalid name or description
    Given I am logged in with email "a@gmail.com" and password "Password1!"
    And I have an image of a plant
    When the app identifies the plant image
    And the app displays the name and relevant details after identifying
    And I input a <name> and <description>
    Then If the details are invalid, I get the appropriate <message>
    Examples:
      | name                                                                | description                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                         | message                                                                                                                                                   |
      | "Timothy"                                                           | "abcabcabcabcabcabcabcabcabcabcabcabcabcabcabcabcabcabcabcabcabcabcabcabcabcabcabcabcabcabcabcabcabcabcabcabcabcabcabcabcabcabcabcabcabcabcabcabcabcabcabcabcabcabcabcabcabcabcabcabcabcabcabcabcabcabcabcabcabcabcabcabcabcabcabcabcabcabcabcabcabcabcabcabcabcabcabcabcabcabcabcabcabcabcabcabcabcabcabcabcabcabcabcabcabcabcabcabcabcabcabcabcabcabcabcabcabcabcabcabcabcabcabcabcabcabcabcabcabcabcabcabcabcabcabcabcabcabcabcabcabcabcabcabcabcabcabcabcabcabcabcabcabcabcabcabcabcabcabcabcabcabcabcabcabcabcabcabcabcabcaNOT" | "Plant description must be 512 characters or less <br/>"                                                                                                      |
      | "Lorem ipsum dolor sit amet, consectetuer adipiscing elit. Aenean@" | "Apple"                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                             | "Plant name must be 64 characters long or less <br/>Plant name cannot be empty and must only include letters, spaces, hyphens or apostrophes <br/>" |
      | "😀"                                                                | "Apple"                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                             | "Plant name cannot be empty and must only include letters, spaces, hyphens or apostrophes <br/>Plant name must include at least one letter"                                            |
