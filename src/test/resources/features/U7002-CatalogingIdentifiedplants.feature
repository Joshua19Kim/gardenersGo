Feature: U7002 - As Jackie Wei, I want to catalog identified plants into my personal collection, so that I can keep track of the plants I've discovered.

  @7002
    Scenario: #AC2 - View plants belonging to a species
    Given I am logged in with email "a@gmail.com" and password "Password1!"
    When I click on an entry for a plant on my collections page
    Then I see a list of all the unique plants that I have collected before that belong to that plant species

  @U7002
  Scenario Outline: #AC3 - Save an identified plant
    Given I am logged in with email "a@gmail.com" and password "Password1!"
    And I have an image of a plant
    When the app identifies the plant image
    And the app displays the name and relevant details after identifying
    And I input a <name>, <description>, <latitude> and <longitude>
    Then my plant is saved
    Examples:
      | name      | description                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                        | latitude | longitude |
      | "Timothy" | "Apple"                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                            | "80"     | "179"     |
      | "Timothy" | "abcabcabcabcabcabcabcabcabcabcabcabcabcabcabcabcabcabcabcabcabcabcabcabcabcabcabcabcabcabcabcabcabcabcabcabcabcabcabcabcabcabcabcabcabcabcabcabcabcabcabcabcabcabcabcabcabcabcabcabcabcabcabcabcabcabcabcabcabcabcabcabcabcabcabcabcabcabcabcabcabcabcabcabcabcabcabcabcabcabcabcabcabcabcabcabcabcabcabcabcabcabcabcabcabcabcabcabcabcabcabcabcabcabcabcabcabcabcabcabcabcabcabcabcabcabcabcabcabcabcabcabcabcabcabcabcabcabcabcabcabcabcabcabcabcabcabcabcabcabcabcabcabcabcabcabcabcabcabcabcabcabcabcabcabcabcabcabcabcabca" | "-80"     | "-179"|
      | "Timothy" | ""                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                 | "75"     | "80"  |


  @U7002
  Scenario Outline: #AC3 - Invalid name or description
    Given I am logged in with email "a@gmail.com" and password "Password1!"
    And I have an image of a plant
    When the app identifies the plant image
    And the app displays the name and relevant details after identifying
    And I input a <name>, <description>, <latitude> and <longitude>
    Then If the details are invalid, I get the appropriate <message>
    Examples:
      | name                                                                | description                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                         | latitude | longitude | message                                                                                                                                                   |
      | "Timothy"                                                           | "abcabcabcabcabcabcabcabcabcabcabcabcabcabcabcabcabcabcabcabcabcabcabcabcabcabcabcabcabcabcabcabcabcabcabcabcabcabcabcabcabcabcabcabcabcabcabcabcabcabcabcabcabcabcabcabcabcabcabcabcabcabcabcabcabcabcabcabcabcabcabcabcabcabcabcabcabcabcabcabcabcabcabcabcabcabcabcabcabcabcabcabcabcabcabcabcabcabcabcabcabcabcabcabcabcabcabcabcabcabcabcabcabcabcabcabcabcabcabcabcabcabcabcabcabcabcabcabcabcabcabcabcabcabcabcabcabcabcabcabcabcabcabcabcabcabcabcabcabcabcabcabcabcabcabcabcabcabcabcabcabcabcabcabcabcabcabcabcabcabcaNOT" | "80"    | "179"     | "Plant description must be 512 characters or less <br/>"                                                                                                      |
      | "Lorem ipsum dolor sit amet, consectetuer adipiscing elit. Aenean@" | "Apple"                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                             | "-80"    | "-179"    |"Plant name must be 64 characters long or less <br/>Plant name cannot be empty and must only include letters, spaces, hyphens or apostrophes <br/>" |
      | "😀"                                                                | "Apple"                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                             | "75"     | "80 "     | "Plant name cannot be empty and must only include letters, spaces, hyphens or apostrophes <br/>Plant name must include at least one letter"                                            |
      | "plant name" | "my plant" | "180" | "150" | "Invalid Location" |
      | "plant name" | "my plant" | "50" | "300" | "Invalid Location" |
      | "plant name" | "my plant" | "sgsdgdahad" | "sdgsdgsDG" | "Invalid Location" |